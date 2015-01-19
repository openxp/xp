module api.ui.dialog {

    export interface ModalDialogConfig {
        title: api.ui.dialog.ModalDialogHeader;
    }

    export class ModalDialog extends api.dom.DivEl {

        private config: ModalDialogConfig;

        private title: api.ui.dialog.ModalDialogHeader;

        private contentPanel: ModalDialogContentPanel;

        private buttonRow: ModalDialogButtonRow;

        private cancelAction: api.ui.Action;

        private actions: api.ui.Action[] = [];

        private mouseClickListener: {(MouseEvent): void};

        constructor(config: ModalDialogConfig) {
            super("modal-dialog");

            this.config = config;

            this.title = this.config.title;
            this.appendChild(this.title);

            this.contentPanel = new ModalDialogContentPanel();
            this.appendChild(this.contentPanel);

            this.buttonRow = new ModalDialogButtonRow();
            this.appendChild(this.buttonRow);

            this.mouseClickListener = (event: MouseEvent) => {
                if (this.isVisible()) {
                    for (var element = event.target; element; element = (<any>element).parentNode) {
                        if (element == this.getHTMLElement()) {
                            return;
                        }
                    }
                    if (this.cancelAction) {
                        this.cancelAction.execute();
                    }
                }
            };

            api.dom.Body.get().onMouseDown(this.mouseClickListener);

            this.onRemoved(() => api.dom.Body.get().unMouseDown(this.mouseClickListener));
            this.onAdded(() => api.dom.Body.get().onMouseDown(this.mouseClickListener));
        }

        setCancelAction(action: api.ui.Action) {
            this.cancelAction = action;
            this.addAction(action);
        }

        getCancelAction(): api.ui.Action {
            return this.cancelAction;
        }

        setTitle(value: string) {
            this.title.setTitle(value);
        }

        appendChildToContentPanel(child: api.dom.Element) {
            this.contentPanel.appendChild(child);
        }

        removeChildFromContentPanel(child: api.dom.Element) {
            this.contentPanel.removeChild(child);
        }

        addAction(action: api.ui.Action, useDefault?: boolean, prepend?: boolean) {
            this.actions.push(action);
            this.buttonRow.addAction(action, useDefault, prepend);
        }

        show() {
            super.show();
            this.centerMyself();
            this.buttonRow.focusDefaultAction();
        }

        private centerMyself() {
            var el = this.getEl();
            el.setMarginLeft("-" + (el.getWidthWithBorder() / 2) + "px").
                setMarginTop("-" + (el.getHeightWithBorder() / 2) + "px");
        }

        hide() {
            super.hide();
        }

        close() {

            api.ui.mask.BodyMask.get().hide();

            this.hide();

            api.ui.KeyBindings.get().unshelveBindings();
        }

        open() {

            api.ui.mask.BodyMask.get().show();

            api.ui.KeyBindings.get().shelveBindings();

            this.show();

            api.ui.KeyBindings.get().bindKeys(api.ui.Action.getKeyBindings(this.actions));
        }
    }

    export class ModalDialogHeader extends api.dom.DivEl {

        private titleEl: api.dom.H2El;

        constructor(title: string) {
            super("dialog-header");

            this.titleEl = new api.dom.H2El('title');
            this.titleEl.setHtml(title);
            this.appendChild(this.titleEl);
        }

        setTitle(value: string) {
            this.titleEl.setHtml(value);
        }
    }

    export class ModalDialogContentPanel extends api.dom.DivEl {

        constructor() {
            super("dialog-content");
        }
    }

    export class ModalDialogButtonRow extends api.dom.DivEl {

        private defaultButton: DialogButton;

        private buttonContainer: api.dom.DivEl;

        constructor() {
            super("dialog-buttons");

            this.buttonContainer = new api.dom.DivEl('button-container');
            this.appendChild(this.buttonContainer);
        }

        addAction(action: api.ui.Action, useDefault?: boolean, prepend?: boolean): DialogButton {
            var button = new DialogButton(action);
            if (useDefault) {
                this.defaultButton = button;
            }
            if (prepend) {
                this.buttonContainer.prependChild(button);
            } else {
                this.buttonContainer.appendChild(button);
            }

            action.onPropertyChanged(() => {
                button.setLabel(action.getLabel());
                button.setEnabled(action.isEnabled());
            });
            return button;
        }

        focusDefaultAction() {
            if (this.defaultButton) {
                this.defaultButton.giveFocus();
            }
        }
    }

}