module api.content.site.inputtype.moduleconfigurator {

    import PropertyTree = api.data2.PropertyTree;
    import Property = api.data2.Property;
    import PropertyArray = api.data2.PropertyArray;
    import Value = api.data2.Value;
    import ValueType = api.data2.ValueType;
    import ValueTypes = api.data2.ValueTypes;
    import ValueChangedEvent = api.form.inputtype.support.ValueChangedEvent;
    import InputOccurrences = api.form.inputtype.support.InputOccurrences;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import ModuleConfigBuilder = api.content.site.ModuleConfigBuilder;
    import GetModuleRequest = api.module.GetModuleRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class ModuleConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<ModuleView> {

        private context: api.form.inputtype.InputTypeViewContext<any>;

        private input: api.form.Input;

        private propertyArray: PropertyArray;

        private comboBox: ModuleConfiguratorComboBox;

        private layoutInProgress: boolean;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.form.inputtype.InputTypeViewContext<any>) {
            super("module-configurator");
            this.context = config;
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray) {

            this.layoutInProgress = true;
            this.input = input;
            this.propertyArray = propertyArray;

            var moduleConfigProvider = new ModuleConfigProvider(propertyArray);
            this.comboBox = this.createComboBox(input, moduleConfigProvider);

            this.appendChild(this.comboBox);

            this.doLoadModules(propertyArray).
                catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(()=> {
                    this.layoutInProgress = false;
                }).done();
        }

        private doLoadModules(propertyArray: PropertyArray): wemQ.Promise<Module[]> {
            var promises: wemQ.Promise<Module>[] = [];
            propertyArray.forEach((property: Property) => {

                if (property.hasNonNullValue()) {
                    var moduleConfig = new ModuleConfigBuilder().fromData(property.getSet()).build();
                    var promise = new GetModuleRequest(moduleConfig.getModuleKey()).sendAndParse();
                    promises.push(promise);
                    promise.then((requestedModule: Module) => {
                        this.comboBox.select(requestedModule);
                    }).done();

                }
            });
            return wemQ.all<Module>(promises);
        }

        private createComboBox(input: api.form.Input, moduleConfigProvider: ModuleConfigProvider): ModuleConfiguratorComboBox {

            var comboBox = new ModuleConfiguratorComboBox(input.getOccurrences().getMaximum() || 0, moduleConfigProvider);

            comboBox.onOptionDeselected((removed: SelectedOption<Module>) => {
                this.propertyArray.remove(removed.getIndex());
                this.validate(false);
            });

            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<Module>) => {
                if (!this.layoutInProgress) {
                    var key = event.getOption().displayValue.getModuleKey();
                    if (!key) {
                        return;
                    }

                    var selectedOption = comboBox.getSelectedOption(event.getOption());
                    var selectedOptionView: ModuleConfiguratorSelectedOptionView = <ModuleConfiguratorSelectedOptionView>selectedOption.getOptionView();
                    var configData = selectedOptionView.getFormView().getData();

                    var moduleConfig = new ModuleConfigBuilder().
                        setModuleKey(key).
                        setConfig(configData).
                        build();

                    var moduleConfigAsData = moduleConfig.toPropertySet(this.propertyArray.newSet());
                    var newValue = new Value(moduleConfigAsData, ValueTypes.DATA);

                    if (comboBox.countSelected() == 1) { // overwrite initial value
                        this.propertyArray.set(0, newValue);
                    }
                    else {
                        this.propertyArray.add(newValue);
                    }
                }
                this.validate(false);
            });

            return comboBox;
        }

        availableSizeChanged() {
        }


        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            // check the number of occurrencees
            var numberOfValids = this.comboBox.countSelected();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            // check
            this.comboBox.getSelectedOptionViews().forEach((view: ModuleConfiguratorSelectedOptionView) => {
                view.getFormView().displayValidationErrors(true);
                var validationRecording = view.getFormView().validate(silent);
                if (!validationRecording.isMinimumOccurrencesValid()) {
                    recording.setBreaksMinimumOccurrences(true);
                }
                if (!validationRecording.isMaximumOccurrencesValid()) {
                    recording.setBreaksMaximumOccurrences(true);
                }
                console.log(validationRecording);
            });

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ModuleConfigurator", ModuleConfigurator));
}