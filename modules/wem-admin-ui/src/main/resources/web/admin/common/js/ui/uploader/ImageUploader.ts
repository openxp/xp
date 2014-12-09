module api.ui.uploader {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;

    export interface ImageUploaderConfig extends FileUploaderConfig {
    }

    export class ImageUploader extends FileUploader {

        private images: api.dom.ImgEl[];

        constructor(config: ImageUploaderConfig) {
            this.images = [];

            if (!config.allowTypes) {
                config.allowTypes = [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ];
            }

            super(config);
            this.addClass('image-uploader');
        }

        setValue(value: string): ImageUploader {
            super.setValue(value);

            var results = this.getResultContainer();
            results.removeChildren();
            this.images.length = 0;

            var values = [].concat(JSON.parse(value));
            values.forEach((val) => {
                results.appendChild(this.createImageResult(val));
            });

            return this;
        }

        private createImageResult(url: string): api.dom.DivEl {
            var container = new api.dom.DivEl();

            var src: string;
            if (url && (url.indexOf('/') == -1)) {
                src = api.util.UriHelper.getRestUri(url ? 'blob/' + url + '?mimeType=image/png' : 'common/images/x-user-photo.png');
            } else {
                src = url;
            }

            var image = new api.dom.ImgEl(src);

            image.onClicked((event: MouseEvent) => {
                image.toggleClass('selected');

                event.stopPropagation();
                event.preventDefault();
            });

            container.appendChild(image);
            this.images.push(image);

            api.dom.Body.get().onClicked((event: MouseEvent) => {
                this.images.forEach((img) => {
                    img.removeClass('selected');
                });
            });

            var toolbar = new api.dom.DivEl('toolbar');
            container.appendChild(toolbar);

            var crop = new Button().addClass("icon-crop2");
            var rotateLeft = new Button().addClass("icon-rotate");
            var rotateRight = new Button().addClass("icon-rotate2");
            var flipHorizontal = new Button().addClass("icon-flip");
            var flipVertical = new Button().addClass("icon-flip2");
            var palette = new Button().addClass("icon-palette");

            toolbar.appendChildren([crop, rotateLeft, rotateRight, flipHorizontal, flipVertical, palette]);

            return container;
        }

    }
}