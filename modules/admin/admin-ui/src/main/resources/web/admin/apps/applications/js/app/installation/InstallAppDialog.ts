module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import GetNearestSiteRequest = api.content.GetNearestSiteRequest;
    import ContentName = api.content.ContentName;
    import Content = api.content.Content;
    import ContentPath = api.content.ContentPath;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import ContentType = api.schema.content.ContentType;
    import Site = api.content.site.Site;
    import ApplicationKey = api.application.ApplicationKey;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import UploadItem = api.ui.uploader.UploadItem;
    import ListContentByPathRequest = api.content.ListContentByPathRequest;

    export class InstallAppDialog extends api.ui.dialog.ModalDialog {

        private contentDialogTitle: InstallAppDialogTitle;

        private contentList: NewContentDialogList;

        private contentListMask: api.ui.mask.LoadMask;

        private fileInput: api.ui.text.FileInput;

        private mediaUploaderEl: api.content.MediaUploaderEl;

        private listItems: NewContentDialogListItem[];
        private mostPopularItems: MostPopularItem[];

        private uploaderEnabled: boolean;

        private mockModalDialog: InstallAppDialog; //used to calculate modal window height for smooth animation

        constructor() {
            this.contentDialogTitle = new InstallAppDialogTitle("Create Content", "");

            super({
                title: this.contentDialogTitle
            });

            this.uploaderEnabled = true;

            this.listItems = [];

            this.addClass("new-content-dialog hidden");

            this.initContentList();

            this.initFileInput();

            this.initMediaUploader();

            this.initLoadingMasks();

            api.dom.Body.get().appendChild(this);
        }

        private initContentList() {
            this.contentList = new app.create.NewContentDialogList();

            this.contentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });
        }


        private initFileInput() {
            this.fileInput = new api.ui.text.FileInput('large').setPlaceholder("Search for content types").setUploaderParams({
                parent: ContentPath.ROOT.toString()
            });

            this.fileInput.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            this.fileInput.onInput((event: Event) => {

            });

            this.fileInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });
        }

        private initMediaUploader() {

            var uploaderContainer = new api.dom.DivEl('uploader-container');
            this.appendChild(uploaderContainer);

            var uploaderMask = new api.dom.DivEl('uploader-mask');
            uploaderContainer.appendChild(uploaderMask);

            this.mediaUploaderEl = new api.content.MediaUploaderEl({
                operation: api.content.MediaUploaderElOperation.create,
                params: {
                    parent: ContentPath.ROOT.toString()
                },
                name: 'new-content-uploader',
                showResult: false,
                allowMultiSelection: true,
                deferred: true  // wait till the window is shown
            });
            uploaderContainer.appendChild(this.mediaUploaderEl);

            this.mediaUploaderEl.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                if (this.uploaderEnabled) {
                    var target = <HTMLElement> event.target;

                    if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                        uploaderContainer.show();
                    }
                    dragOverEl = target;
                }
            });

            this.onDragLeave((event: DragEvent) => {
                if (this.uploaderEnabled) {
                    var targetEl = <HTMLElement> event.target;

                    if (dragOverEl == targetEl) {
                        uploaderContainer.hide();
                    }
                }
            });

            this.onDrop((event: DragEvent) => {
                if (this.uploaderEnabled) {
                    uploaderContainer.hide();
                }

            });
        }

        private initLoadingMasks() {
            this.contentListMask = new api.ui.mask.LoadMask(this.contentList);
        }

        private closeAndFireEventFromMediaUpload(items: UploadItem<Content>[]) {
            this.close();
        }

        private closeAndFireEventFromContentType(item: NewContentDialogListItem) {
            this.close();
        }

        open() {
            super.open();
            var keyBindings = [
                new api.ui.KeyBinding('up', () => {
                    api.dom.FormEl.moveFocusToPrevFocusable(api.dom.Element.fromHtmlElement(<HTMLElement>document.activeElement),
                        "input,li");
                }).setGlobal(true),
                new api.ui.KeyBinding('down', () => {
                    api.dom.FormEl.moveFocusToNextFocusable(api.dom.Element.fromHtmlElement(<HTMLElement>document.activeElement),
                        "input,li");
                }).setGlobal(true)];

            api.ui.KeyBindings.get().bindKeys(keyBindings);
        }

        show() {
            this.updateDialogTitlePath();

            this.toggleUploaderEnabled();
            this.resetFileInputWithUploader();

            super.show();

            this.fileInput.giveFocus();

            if (this.mockModalDialog == null) {
                this.createMockDialog();
            }
        }

        hide() {
            super.hide();
            this.mediaUploaderEl.stop();
            this.addClass("hidden");
            this.removeClass("animated");
        }

        close() {
            this.fileInput.reset();
            super.close();
        }

        private showLoadingMasks() {
            this.contentList.insertChild(this.contentListMask, 0);
            this.contentListMask.show();
        }

        private hideLoadingMasks() {
            this.contentListMask.hide();
        }

        private showMockDialog() {
            wemjq(this.getEl().getHTMLElement()).show();
        }

        private handleModalDialogAnimation() {

            this.updateMockDialogTitlePath();

            this.addClass("animated");
            this.removeClass("hidden");

            this.alignDialogWindowVertically();
        }

        private updateMockDialogTitlePath() {
            if (this.parentContent) {
                this.mockModalDialog.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.mockModalDialog.contentDialogTitle.setPath('');
            }
        }

        private updateDialogTitlePath() {
            if (this.parentContent) {
                this.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.contentDialogTitle.setPath('');
            }
        }

        private toggleUploaderEnabled() {
            this.uploaderEnabled = !this.parentContent || !this.parentContent.getType().isTemplateFolder();

            if (this.uploaderEnabled) {
                this.removeClass("no-uploader-el");
            } else {
                this.addClass("no-uploader-el");
            }
        }

        private resetFileInputWithUploader() {
            this.mediaUploaderEl.reset();
            this.fileInput.reset();
            this.mediaUploaderEl.setEnabled(this.uploaderEnabled);
            this.fileInput.getUploader().setEnabled(this.uploaderEnabled);
        }

        private createMockDialog() {
            this.mockModalDialog = new NewContentDialog();
            this.mockModalDialog.close = function () {
                wemjq(this.getEl().getHTMLElement()).hide();
            };
            this.getParentElement().appendChild(this.mockModalDialog);
            this.mockModalDialog.addClass("mock-modal-dialog");
            this.mockModalDialog.removeClass("hidden");
        }

        private alignDialogWindowVertically() {
            this.getEl().setMarginTop("-" + ( this.mockModalDialog.getEl().getHeightWithBorder() / 2) + "px");
        }
    }

    export class InstallAppDialogTitle extends api.ui.dialog.ModalDialogHeader {

        private pathEl: api.dom.PEl;

        constructor(title: string, path: string) {
            super(title);

            this.pathEl = new api.dom.PEl('path');
            this.pathEl.setHtml(path);
            this.appendChild(this.pathEl);
        }

        setPath(path: string) {
            this.pathEl.setHtml(path).setVisible(!api.util.StringHelper.isBlank(path));
        }
    }
}
