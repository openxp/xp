interface DocumentSize {
    width: number;
    height: number;
}

interface ViewPortSize {
    width: number;
    height: number;
}


module LiveEdit {
    export class DomHelper {

        static $ = $liveEdit;

        public static getDocumentSize():DocumentSize {
            var $document = $(document);
            return {
                width: $document.width(),
                height: $document.height()
            };
        }

        public static getViewPortSize():ViewPortSize {
            var win:JQuery = $(window);
            return {
                width: win.width(),
                height: win.height()
            };
        }

        public static getDocumentScrollTop():number {
            return $(document).scrollTop();
        }

    }
}