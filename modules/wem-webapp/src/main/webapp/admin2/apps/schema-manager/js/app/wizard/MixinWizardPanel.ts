module app_wizard {

    export class MixinWizardPanel extends api_app_wizard.WizardPanel {

        private static DEFAULT_CHEMA_ICON_URL:string = '/admin/rest/schema/image/Mixin:_:_';

        private saveAction:api_ui.Action;

        private closeAction:api_ui.Action;

        private formIcon:api_app_wizard.FormIcon;

        private toolbar:api_ui_toolbar.Toolbar;

        private persistedMixin:api_remote_mixin.Mixin;

        private mixinForm:MixinForm;

        constructor(id:string) {
            this.formIcon = new api_app_wizard.FormIcon(MixinWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon", "rest/upload");

            this.closeAction = new api_app_wizard.CloseAction(this);
            this.saveAction = new api_app_wizard.SaveAction(this);

            this.toolbar = new MixinWizardToolbar({
                saveAction: this.saveAction,
                closeAction: this.closeAction
            });

            super({
                formIcon: this.formIcon,
                toolbar: this.toolbar,
                saveAction: this.saveAction
            });

            this.setDisplayName("New Mixin");
            this.setName(this.generateName(this.getDisplayName()));
            this.setAutogenerateDisplayName(true);
            this.setAutogenerateName(true);

            this.mixinForm = new MixinForm();
            this.addStep(new api_app_wizard.WizardStep("Mixin", this.mixinForm));
        }

        setPersistedItem(mixin:api_remote_mixin.Mixin) {
            super.setPersistedItem(mixin);

            this.setDisplayName(mixin.displayName);
            this.setName(mixin.name);
            this.formIcon.setSrc(mixin.iconUrl);

            this.setAutogenerateDisplayName(!mixin);
            this.setAutogenerateName(!mixin.name || mixin.name == this.generateName(mixin.displayName));

            this.persistedMixin = mixin;
        }

        persistNewItem(successCallback?:() => void) {
            var createParams:api_remote_mixin.CreateOrUpdateParams = {
                mixin: this.mixinForm.getXml(),
                iconReference: this.getIconUrl()
            };

            api_remote_mixin.RemoteMixinService.mixin_createOrUpdate(createParams, () => {
                new app_wizard.MixinCreatedEvent().fire();
                api_notify.showFeedback('Mixin was created!');
            });
        }

        updatePersistedItem(successCallback?:() => void) {
            var updateParams:api_remote_mixin.CreateOrUpdateParams = {
                mixin: this.mixinForm.getXml(),
                iconReference: this.getIconUrl()
            };

            api_remote_mixin.RemoteMixinService.mixin_createOrUpdate(updateParams, () => {
                new app_wizard.MixinUpdatedEvent().fire();
                api_notify.showFeedback('Mixin was saved!');
            });
        }
    }
}