module api.content.page.layout.json {

    export interface LayoutDescriptorJson extends api.content.page.json.DescriptorJson {

        regions:api.content.page.region.json.RegionsDescriptorJson
    }
}