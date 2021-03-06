/**
 * Thymeleaf template related functions.
 *
 * @example
 * var thymeleafLib = require('/lib/xp/thymeleaf');
 *
 * @module lib/xp/thymeleaf
 */

var service = __.newBean('com.enonic.xp.lib.thymeleaf.ThymeleafService');

/**
 * This function renders a view using thymeleaf.
 *
 * @example-ref examples/thymeleaf/render.js
 *
 * @param view Location of the view. Use `resolve(..)` to resolve a view.
 * @param {object} model Model that is passed to the view.
 *
 * @returns {string} The rendered output.
 */
exports.render = function (view, model) {
    var processor = service.newProcessor();
    processor.view = view;
    processor.model = __.toScriptValue(model);
    return processor.process();
};
