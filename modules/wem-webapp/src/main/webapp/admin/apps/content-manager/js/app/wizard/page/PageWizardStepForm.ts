module app.wizard.page {

    export interface PageWizardStepFormConfig {

        parentContent: api.content.Content;

        siteTemplate: api.content.site.template.SiteTemplate;

    }

    export class PageWizardStepForm extends api.app.wizard.WizardStepForm {

        private parentContent: api.content.Content;

        private siteTemplate: api.content.site.template.SiteTemplate;

        private content: api.content.Content;

        private pageTemplateSelectorForm: PageTemplateSelector;

        private selectedPageTemplate: api.content.page.PageTemplate;

        private formView: api.form.FormView;

        private configFormWrapper: api.dom.DivEl;

        constructor(config: PageWizardStepFormConfig) {
            super();
            this.addClass("page-wizard-step-form");
            this.parentContent = config.parentContent;
            this.siteTemplate = config.siteTemplate;

            this.pageTemplateSelectorForm = new PageTemplateSelector(this);
            this.pageTemplateSelectorForm.addPageTemplateChangedListener((changedTo: api.content.page.PageTemplateSummary) => {
                this.handlePageTemplateChanged(changedTo);
            });

            this.appendChild(this.pageTemplateSelectorForm);

            this.configFormWrapper = new api.dom.DivEl();
            this.appendChild(this.configFormWrapper);
        }

        getContent():api.content.Content {
            return this.content;
        }

        layout(content: api.content.Content, siteContent: api.content.Content): Q.Promise<void> {

            console.log("PageWizardStepForm.layout() ... ");
            var deferred = Q.defer<void>();

            this.content = content;

            var page: api.content.page.Page = content.getPage();

            if (page != null && page.getTemplate() != null) {

                new api.content.page.GetPageTemplateByKeyRequest(page.getTemplate()).
                    setSiteTemplateKey(this.siteTemplate.getKey()).
                    sendAndParse().
                    done((pageTemplate: api.content.page.PageTemplate) => {

                        this.selectedPageTemplate = pageTemplate;
                        this.pageTemplateSelectorForm.layoutExisting(siteContent.getSite().getTemplateKey(), page.getTemplate()).
                            done(()=> {

                                deferred.resolve(null);
                            });
                    });
            }
            else {

                this.pageTemplateSelectorForm.layoutExisting(siteContent.getSite().getTemplateKey(), null).
                    done(()=> {

                        deferred.resolve(null);
                    });
            }

            return deferred.promise;
        }

        private handlePageTemplateChanged(changedTo: api.content.page.PageTemplateSummary) {

            console.log("PageWizardStepForm.handlePageTemplateChanged() ... ");

            if (changedTo == null) {
                this.selectedPageTemplate = null;
                this.configFormWrapper.removeChildren();
                console.log("PageWizardStepForm.handlePageTemplateChanged() ... changed to null");
            }
            else {
                console.log("PageWizardStepForm.handlePageTemplateChanged() ... changed to something (loading...)");
                new api.content.page.GetPageTemplateByKeyRequest(changedTo.getKey()).
                    setSiteTemplateKey(this.siteTemplate.getKey()).
                    sendAndParse().
                    done((pageTemplate: api.content.page.PageTemplate) => {

                        this.selectedPageTemplate = pageTemplate;

                        this.layoutPageTemplateForm(pageTemplate);
                    });
            }
        }

        private layoutPageTemplateForm(pageTemplate: api.content.page.PageTemplate) {

            var formContext = new api.form.FormContextBuilder().build();

            var form = pageTemplate.getDescriptor().getConfig();
            var config = pageTemplate.getConfig();
            if (this.content.isPage() && this.content.getPage().hasConfig()) {
                config = this.content.getPage().getConfig();
            }
            this.formView = new api.form.FormView(formContext, form, config);
            this.configFormWrapper.removeChildren();
            this.configFormWrapper.appendChild(this.formView);

        }

        public getPageTemplate(): api.content.page.PageTemplate {
            return this.selectedPageTemplate;
        }

        public getConfig(): api.data.RootDataSet {
            if (this.formView == null) {
                return new api.data.RootDataSet();
            }
            var config = this.formView.getData();
            return  config;
        }

    }
}