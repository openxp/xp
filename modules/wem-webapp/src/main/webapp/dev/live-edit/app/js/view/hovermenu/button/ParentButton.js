(function ($) {
    'use strict';

    // Class definition (constructor function)
    var parentButton = AdminLiveEdit.view.hovermenu.button.ParentButton = function (hoverMenu) {
        this.hoverMenu = hoverMenu;
        this.init();
    };

    // Inherits ui.Button
    parentButton.prototype = new AdminLiveEdit.view.Button();

    // Fix constructor as it now is Button
    parentButton.constructor = parentButton;

    // Shorthand ref to the prototype
    var p = parentButton.prototype;


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    p.init = function () {
        var me = this;
        var $button = me.createButton({
            id: 'live-edit-button-parent',
            text: 'Parent',
            cls: 'live-edit-hover-menu-button',
            iconCls: 'live-edit-icon-parent',
            handler: function (event) {
                event.stopPropagation();
                var $parent = me.hoverMenu.$currentComponent.parents('[data-live-edit-type]');
                if ($parent && $parent.length > 0) {
                    $(window).trigger('component:select', [$($parent[0])]);
                }
            }
        });

        me.appendTo(this.hoverMenu.getEl());
        me.hoverMenu.buttons.push(me);
    };

}($liveedit));