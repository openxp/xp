module app {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import ContentSummary = api.content.ContentSummary;
    import Content = api.content.Content;
    import ContentId = api.content.ContentId;
    import ContentNamedEvent = api.content.ContentNamedEvent;
    import ContentUpdatedEvent = api.content.ContentUpdatedEvent;
    import AppBarTabId = api.app.bar.AppBarTabId;
    import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
    import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
    import ShowBrowsePanelEvent = api.app.bar.event.ShowBrowsePanelEvent;

    export class ContentAppPanel extends api.app.BrowseAndWizardBasedAppPanel<ContentSummary> {

        private mask: api.ui.mask.LoadMask;

        constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

            super({
                appBar: appBar
            });

            this.mask = new api.ui.mask.LoadMask(this);

            this.handleGlobalEvents();

            this.route(path);
        }

        addWizardPanel(tabMenuItem: AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<Content>) {
            super.addWizardPanel(tabMenuItem, wizardPanel);

            wizardPanel.getHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == "displayName") {
                    tabMenuItem.setLabel(<string>event.getNewValue());
                }
            });
        }

        private route(path?: api.rest.Path) {
            var action = path ? path.getElement(0) : undefined;

            switch (action) {
            case 'edit':
                var id = path.getElement(1);
                if (id) {
                    new api.content.GetContentByIdRequest(new ContentId(id)).sendAndParse().
                        done((content: Content) => {
                            new app.browse.EditContentEvent([content]).fire();
                        });
                }
                break;
            case 'view' :
                var id = path.getElement(1);
                if (id) {
                    new api.content.GetContentByIdRequest(new ContentId(id)).sendAndParse().
                        done((content: Content) => {
                            new app.browse.ViewContentEvent([content]).fire();
                        });
                }
                break;
            default:
                new ShowBrowsePanelEvent().fire();
                break;
            }
        }

        private handleGlobalEvents() {
            app.create.NewContentEvent.on((event) => {
                this.handleNewContent(event);
            });

            app.browse.ViewContentEvent.on((event) => {
                this.handleView(event);
            });

            app.browse.EditContentEvent.on((event) => {
                this.handleEdit(event);
            });

            ShowBrowsePanelEvent.on((event) => {
                this.handleBrowse(event);
            });

            ContentUpdatedEvent.on((event) => {
                this.handleUpdated(event);
            });

            app.browse.SortContentEvent.on((event) => {
                this.handleSort(event);
            });

            app.browse.MoveContentEvent.on((event) => {
                this.handleMove(event);
            });
        }

        private handleUpdated(event: ContentUpdatedEvent) {
            // do something when content is updated
        }

        private handleBrowse(event: ShowBrowsePanelEvent) {
            var browsePanel: api.app.browse.BrowsePanel<ContentSummary> = this.getBrowsePanel();
            if (!browsePanel) {
                this.addBrowsePanel(new app.browse.ContentBrowsePanel());
            } else {
                this.selectPanelByIndex(this.getPanelIndex(browsePanel));
            }
        }

        private handleNewContent(newContentEvent: app.create.NewContentEvent) {

            var contentTypeSummary = newContentEvent.getContentType();
            var tabId = AppBarTabId.forNew(contentTypeSummary.getName());
            var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);
            } else {
                this.mask.show();
                var contentWizardPanelFactory = new app.wizard.ContentWizardPanelFactory().
                    setAppBarTabId(tabId).
                    setParentContent(newContentEvent.getParentContent()).
                    setContentTypeName(contentTypeSummary.getContentTypeName());

                contentWizardPanelFactory.setCreateSite(newContentEvent.getContentType().isSite());
                contentWizardPanelFactory.createForNew().then((wizard: app.wizard.ContentWizardPanel) => {
                    tabMenuItem = new AppBarTabMenuItemBuilder().setLabel("[New " + contentTypeSummary.getDisplayName() + "]").
                        setTabId(tabId).
                        setCloseAction(wizard.getCloseAction()).
                        build();

                    wizard.onContentNamed((event: ContentNamedEvent) => {
                        this.handleContentNamedEvent(event);
                    });

                    this.addWizardPanel(tabMenuItem, wizard);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.mask.hide();
                }).done();
            }
        }


        private handleEdit(event: app.browse.EditContentEvent) {

            var contents: ContentSummary[] = event.getModels();
            contents.forEach((content: ContentSummary) => {
                if (!content) {
                    return;
                }
                var closeViewPanelMenuItem = this.resolveTabMenuItemForContentBeingViewed(content);
                var tabMenuItem = this.resolveTabMenuItemForContentBeingEdited(content);

                if (tabMenuItem != null) {
                    this.selectPanel(tabMenuItem);
                } else {
                    this.mask.show();
                    var tabId = AppBarTabId.forEdit(content.getId());

                    new app.wizard.ContentWizardPanelFactory().
                        setAppBarTabId(tabId).
                        setContentToEdit(content.getContentId()).
                        createForEdit().then((wizard: app.wizard.ContentWizardPanel) => {
                            if (closeViewPanelMenuItem != null) {
                                this.getAppBarTabMenu().deselectNavigationItem();
                                this.getAppBarTabMenu().removeNavigationItem(closeViewPanelMenuItem);
                                this.removePanelByIndex(closeViewPanelMenuItem.getIndex());
                            }
                            tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(content.getDisplayName()).
                                setTabId(tabId).
                                setEditing(true).
                                setCloseAction(wizard.getCloseAction()).
                                build();
                            this.addWizardPanel(tabMenuItem, wizard);

                            var viewTabId = AppBarTabId.forView(content.getId());
                            var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
                            if (viewTabMenuItem != null) {
                                this.removePanelByIndex(viewTabMenuItem.getIndex());
                            }
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                            this.mask.hide();
                        }).done();

                }
            });
        }

        private handleView(event: app.browse.ViewContentEvent) {

            var contents: ContentSummary[] = event.getModels();
            contents.forEach((content: ContentSummary) => {
                if (!content) {
                    return;
                }

                var tabMenuItem = this.resolveTabMenuItemForContentBeingEditedOrViewed(content);

                if (tabMenuItem) {
                    this.selectPanel(tabMenuItem);
                } else {
                    var tabId = AppBarTabId.forView(content.getId());
                    var contentItemViewPanel = new app.view.ContentItemViewPanel();
                    tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(content.getDisplayName()).
                        setTabId(tabId).
                        setCloseAction(contentItemViewPanel.getCloseAction()).
                        build();

                    var contentItem = new api.app.view.ViewItem(content)
                        .setDisplayName(content.getDisplayName())
                        .setPath(content.getPath().toString())
                        .setIconUrl(new ContentIconUrlResolver().setContent(content).resolve());

                    contentItemViewPanel.setItem(contentItem);

                    this.addViewPanel(tabMenuItem, contentItemViewPanel);
                }
            });
        }

        private handleSort(event: app.browse.SortContentEvent) {

            var contents: ContentSummary[] = event.getModels();
            new app.browse.OpenSortDialogEvent(contents[0]).fire();
        }

        private handleMove(event: app.browse.MoveContentEvent) {

            var contents: ContentSummary[] = event.getModels();
            new app.browse.OpenMoveDialogEvent(contents[0]).fire();
        }

        private handleContentNamedEvent(event: ContentNamedEvent) {

            var wizard = event.getWizard(),
                tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(wizard.getTabId());
            // update tab id so that new wizard for the same content type can be created
            var newTabId = AppBarTabId.forEdit(event.getContent().getId());
            tabMenuItem.setTabId(newTabId);
            wizard.setTabId(newTabId);
        }

        private resolveTabMenuItemForContentBeingEditedOrViewed(content: ContentSummary): AppBarTabMenuItem {
            var result = this.resolveTabMenuItemForContentBeingEdited(content);
            if (!result) {
                result = this.resolveTabMenuItemForContentBeingViewed(content)
            }
            return result;
        }

        private resolveTabMenuItemForContentBeingEdited(content: ContentSummary): AppBarTabMenuItem {
            if (!!content) {

                var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forEdit(content.getId()));
                if (tabId) {
                    return tabId;
                }
            }
            return null;
        }

        private resolveTabMenuItemForContentBeingViewed(content: ContentSummary): AppBarTabMenuItem {
            if (!!content) {
                var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forView(content.getId()));
                if (tabId) {
                    return tabId;
                }
            }

            return null;
        }
    }

}