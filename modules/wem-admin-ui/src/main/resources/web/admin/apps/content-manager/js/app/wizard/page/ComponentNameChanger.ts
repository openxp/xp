module app.wizard.page {

    import Descriptor = api.content.page.Descriptor;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import Component = api.content.page.Component;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import LayoutRegions = api.content.page.layout.LayoutRegions;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import ComponentPathRegionAndComponent = api.content.page.ComponentPathRegionAndComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageRegions = api.content.page.PageRegions;
    import PageComponentView = api.liveedit.PageComponentView;

    export class ComponentNameChanger {

        private pageRegions: PageRegions;

        private pageComponentView: PageComponentView<Component>;

        setPageRegions(value: PageRegions): ComponentNameChanger {
            this.pageRegions = value;
            return this;
        }

        setComponentView(value: PageComponentView<Component>): ComponentNameChanger {
            this.pageComponentView = value;
            return this;
        }

        changeTo(name: string) {
            api.util.assertNotNull(this.pageRegions, "pageRegions cannot be null");
            api.util.assertNotNull(this.pageComponentView, "pageComponentView cannot be null");

            var component = this.pageComponentView.getPageComponent();

            var componentName = new ComponentName(name);
            component.setName(componentName);
        }
    }
}