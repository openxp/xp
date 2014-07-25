module api.ui.treegrid {

    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class TreeGridToolbar extends api.ui.toolbar.Toolbar {

        constructor(actions: TreeGridActions) {
            super();

            this.addActions(actions.getAllActions());
        }
    }
}
