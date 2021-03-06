module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.region.Component;

    export class ComponentInspectedEvent extends Event {

        private componentView: ComponentView<Component>;

        constructor(componentView?: ComponentView<Component>) {
            super();
            this.componentView = componentView;
        }

        getComponentView(): ComponentView<Component> {
            return this.componentView;
        }

        static on(handler: (event: ComponentInspectedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ComponentInspectedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}