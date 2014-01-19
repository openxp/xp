module api.content.page {

    export class PageDescriptor extends Descriptor {

        private regions: region.RegionDescriptor[];

        constructor(builder: PageDescriptorBuilder) {
            super(builder);
            this.regions = builder.regions;
        }

        public getRegions(): region.RegionDescriptor[] {
            return this.regions;
        }
    }

    export class PageDescriptorBuilder extends DescriptorBuilder {

        regions: region.RegionDescriptor[] = [];

        public fromJson(json: api.content.page.json.PageDescriptorJson): PageDescriptorBuilder {

            this.setName(new DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? new api.form.Form(json.config) : null);

            return this;
        }

        public setName(value: DescriptorName): PageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): PageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): PageDescriptorBuilder {
            this.config = value;
            return this;
        }

        public build(): PageDescriptor {
            return new PageDescriptor(this);
        }
    }
}