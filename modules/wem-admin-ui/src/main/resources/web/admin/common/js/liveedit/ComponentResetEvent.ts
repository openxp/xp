module api.liveedit {

    import Event = api.event.Event;
    import PageComponentView = api.liveedit.PageComponentView;
    import Component = api.content.page.Component;

    export class ComponentResetEvent extends api.event.Event {

        private pageComponentView: PageComponentView<Component>;

        constructor(pageComponentView: PageComponentView<Component>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getComponentView(): PageComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: ComponentResetEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentResetEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}