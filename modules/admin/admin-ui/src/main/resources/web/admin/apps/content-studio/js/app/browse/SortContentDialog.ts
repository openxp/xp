module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import Element = api.dom.Element;
    import SaveSortedContentAction = action.SaveSortedContentAction;
    import ContentSummary = api.content.ContentSummary;
    import ChildOrder = api.content.ChildOrder;
    import OrderChildMovements = api.content.OrderChildMovements;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import DialogButton = api.ui.dialog.DialogButton;

    export class SortContentDialog extends api.ui.dialog.ModalDialog {

        private sortAction: SaveSortedContentAction;

        private parentContent: api.content.ContentSummaryAndCompareStatus;

        private contentGrid: app.browse.SortContentTreeGrid;

        private sortContentMenu: SortContentTabMenu;

        private curChildOrder: ChildOrder;

        private prevChildOrder: ChildOrder;

        private gridDragHandler: ContentGridDragHandler;

        private isOpen: boolean;

        private saveButton: DialogButton;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Sort items")
            });

            this.initTabMenu();

            this.initSortContentMenu();

            this.getEl().addClass("sort-content-dialog");

            this.initSortContentGrid();

            this.initGridDragHandler();

            this.populateContentPanel();

            this.initSaveButtonWithAction();

            OpenSortDialogEvent.on((event) => {
                this.handleOpenSortDialogEvent(event);
            });

            this.addCancelButtonToBottom();
        }

        open() {
            if (!this.isOpen) {
                if (this.contentGrid.getGrid().getDataView().getLength() > 0) {
                    this.contentGrid.getGrid().getEl().setHeightPx(45);//chrome invalid grid render fix
                }
                this.contentGrid.getGrid().resizeCanvas();
                super.open();
                this.isOpen = true;
            }
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            this.remove();
            super.close();
            this.isOpen = false;
            this.contentGrid.setChildOrder(null);
            this.gridDragHandler.clearContentMovements();
        }

        getContent(): ContentSummaryAndCompareStatus {
            return this.parentContent;
        }

        private initSortContentGrid() {
            this.contentGrid = new app.browse.SortContentTreeGrid();
            this.contentGrid.getEl().addClass("sort-content-grid");
            this.contentGrid.onLoaded(() => {
                this.contentGrid.render(true);

                if (this.contentGrid.getContentId()) {
                    this.open();
                }
            });
        }

        private initGridDragHandler() {
            this.gridDragHandler = new ContentGridDragHandler(this.contentGrid);
            this.gridDragHandler.onPositionChanged(() => {
                this.sortContentMenu.selectManualSortingItem();
            });
        }

        private initTabMenu() {
            var menu = new api.ui.tab.TabMenu();
            var tabMenuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("(sorting type)")).build();
            tabMenuItem.setActive(true);
            menu.addNavigationItem(tabMenuItem);
            menu.selectNavigationItem(0);
            menu.show();
        }

        private initSortContentMenu() {
            this.sortContentMenu = new SortContentTabMenu();
            this.sortContentMenu.show();
            this.appendChildToTitle(this.sortContentMenu);

            this.sortContentMenu.onSortOrderChanged(() => {
                this.handleOnSortOrderChangedEvent();
            });
        }

        private initSaveButtonWithAction() {
            this.sortAction = new SaveSortedContentAction(this);

            this.saveButton = this.addAction(this.sortAction);
            this.saveButton.addClass("save-button");

            this.sortAction.onExecuted(() => {
                this.handleSortAction();
            });
        }

        private populateContentPanel() {
            var header = new api.dom.H6El();
            header.setHtml("Sort content by selecting default sort above, or drag and drop for manual sorting");
            this.appendChildToContentPanel(header);
            this.appendChildToContentPanel(this.contentGrid);
        }

        private handleSortAction() {
            if (this.curChildOrder.equals(this.getParentChildOrder()) && !this.curChildOrder.isManual()) {
                this.close();
            } else {
                this.showLoadingSpinner();

                if (this.curChildOrder.isManual()) {
                    this.setManualReorder(this.hasChangedPrevChildOrder() ? this.prevChildOrder : null,
                        this.gridDragHandler.getContentMovements()).done(() => this.onAfterSetOrder());
                } else {
                    this.setContentChildOrder(this.curChildOrder).done(() => this.onAfterSetOrder());
                }
            }
        }

        private handleOpenSortDialogEvent(event) {
            this.parentContent = event.getContent();
            this.curChildOrder = this.getParentChildOrder();
            this.prevChildOrder = undefined;
            this.sortContentMenu.selectNavigationItemByOrder(this.curChildOrder);

            this.contentGrid.reload(this.parentContent);
            if (!this.parentContent.hasChildren()) {
                this.contentGrid.getEl().setAttribute("data-content", event.getContent().getPath().toString());
                this.contentGrid.addClass("no-content");
            } else {
                this.contentGrid.removeClass("no-content");
                this.contentGrid.getEl().removeAttribute("data-content");
            }
        }

        private handleOnSortOrderChangedEvent() {
            var newOrder = this.sortContentMenu.getSelectedNavigationItem().getChildOrder();
            if (!this.curChildOrder.equals(newOrder)) {
                if (!newOrder.isManual()) {
                    this.curChildOrder = newOrder;
                    this.contentGrid.setChildOrder(this.curChildOrder);
                    /*api.content.ContentSummaryAndCompareStatusFetcher.fetch(this.parentContent.getContentId()).
                        done((response: api.content.ContentSummaryAndCompareStatus) => {
                            this.contentGrid.reload(response);
                     });*/
                    this.contentGrid.reload(this.parentContent);
                    this.gridDragHandler.clearContentMovements();
                } else {
                    this.prevChildOrder = this.curChildOrder;
                    this.curChildOrder = newOrder;
                    this.contentGrid.setChildOrder(this.curChildOrder);
                }
            }
        }

        private onAfterSetOrder() {
            this.hideLoadingSpinner();
            this.close();
        }

        private hasChangedPrevChildOrder(): boolean {
            return this.prevChildOrder && !this.prevChildOrder.equals(this.getParentChildOrder());
        }

        private showLoadingSpinner() {
            this.saveButton.addClass("spinner");
        }

        private hideLoadingSpinner() {
            this.saveButton.removeClass("spinner");
        }

        private setContentChildOrder(order: ChildOrder, silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderContentRequest().
                setSilent(silent).
                setContentId(this.parentContent.getContentId()).
                setChildOrder(order).
                sendAndParse();
        }

        private setManualReorder(order: ChildOrder, movements: OrderChildMovements, silent: boolean = false): wemQ.Promise<api.content.Content> {
            return new api.content.OrderChildContentRequest().
                setSilent(silent).
                setManualOrder(true).
                setContentId(this.parentContent.getContentId()).
                setChildOrder(order).
                setContentMovements(movements).
                sendAndParse();
        }

        private getParentChildOrder(): ChildOrder {
            if (this.parentContent && this.parentContent.getContentSummary()) {
                return this.parentContent.getContentSummary().getChildOrder();
            }

            return null;
        }
    }
}