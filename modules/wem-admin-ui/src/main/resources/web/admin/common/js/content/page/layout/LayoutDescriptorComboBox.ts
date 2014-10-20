module api.content.page.layout {

    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import SelectedOptionView = api.ui.selector.combobox.SelectedOptionView;
    import SelectedOptionsView = api.ui.selector.combobox.SelectedOptionsView;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class LayoutDescriptorComboBox extends RichComboBox<LayoutDescriptor> {

        constructor(loader: LayoutDescriptorLoader) {
            super(new RichComboBoxBuilder<LayoutDescriptor>().
                setIdentifierMethod("getKey").
                setOptionDisplayValueViewer(new LayoutDescriptorViewer()).
                setSelectedOptionsView(new LayoutDescriptorSelectedOptionsView()).
                setLoader(loader).
                setMaximumOccurrences(1).
                setNextInputFocusWhenMaxReached(false));
        }

        setDescriptor(key: DescriptorKey) {

            var descriptorToSelect: LayoutDescriptor;

            this.getValues().forEach((descriptor: LayoutDescriptor) => {
                if (descriptor.getKey().toString() == key.toString()) {
                    descriptorToSelect = descriptor;
                }
            });
            if (!descriptorToSelect) {
                return;
            }

            var option: Option<LayoutDescriptor> = {
                value: descriptorToSelect.getKey().toString(),
                displayValue: descriptorToSelect
            };
            this.comboBox.clearSelection();
            this.comboBox.selectOption(option);
        }

        getSelectedOptions(): Option<LayoutDescriptor>[] {
            return this.comboBox.getSelectedOptions();
        }

    }

    export class LayoutDescriptorSelectedOptionsView extends SelectedOptionsView<LayoutDescriptor> {

        createSelectedOption(option: Option<LayoutDescriptor>): SelectedOption<LayoutDescriptor> {
            return new SelectedOption<LayoutDescriptor>(new LayoutDescriptorSelectedOptionView(option), this.count());
        }
    }

    export class LayoutDescriptorSelectedOptionView extends SelectedOptionView<LayoutDescriptor> {

        private descriptor: LayoutDescriptor;

        constructor(option: Option<LayoutDescriptor>) {
            this.descriptor = option.displayValue;
            super(option);
            this.addClass("layout-descriptor-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView.setIconClass("icon-earth icon-medium")
                .setMainName(this.descriptor.getDisplayName())
                .setSubName(this.descriptor.getKey().toString());

            var removeButtonEl = new api.dom.AEl("remove");
            removeButtonEl.onClicked((event: MouseEvent) => {
                this.notifySelectedOptionRemoveRequested();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(namesAndIconView);
        }

    }
}