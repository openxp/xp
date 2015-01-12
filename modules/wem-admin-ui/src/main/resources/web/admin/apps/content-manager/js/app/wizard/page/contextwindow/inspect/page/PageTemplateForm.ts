module app.wizard.page.contextwindow.inspect.page {

    export class PageTemplateForm extends api.ui.form.Form {

        private templateSelector: PageTemplateSelector;

        constructor(templateSelector: PageTemplateSelector) {
            super('form-view');
            this.templateSelector = templateSelector;

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(templateSelector).setLabel("Page template").build());
            this.add(fieldSet);
        }

        getSelector(): PageTemplateSelector {
            return this.templateSelector;
        }

    }
}