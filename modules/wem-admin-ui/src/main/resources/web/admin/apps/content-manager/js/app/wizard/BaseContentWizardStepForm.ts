module app.wizard {

    import ValidationRecording = api.form.ValidationRecording;

    export class BaseContentWizardStepForm extends api.app.wizard.WizardStepForm {

        private validityChangedListeners: {(event: WizardStepValidityChangedEvent): void}[] = [];

        previousValidation: ValidationRecording;

        constructor(className?: string) {
            super(className);
        }

        onValidityChanged(listener: (event: WizardStepValidityChangedEvent) => void) {
            this.validityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: WizardStepValidityChangedEvent) => void) {
            this.validityChangedListeners = this.validityChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        /*
         *   public to be used by inheritors
         */
        notifyValidityChanged(event: WizardStepValidityChangedEvent) {
            this.validityChangedListeners.forEach((listener) => {
                listener(event);
            })
        }

        /*
         *   public to be used by inheritors
         */
        public validate(silent?: boolean): ValidationRecording {
            return new ValidationRecording();
        }

        public isValid(): boolean {
            if (!this.previousValidation) {
                this.previousValidation = this.validate(true);
            }
            return this.previousValidation.isValid();
        }
    }
}