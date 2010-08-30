if (!Core.get(window, [ "JFix", "Sync" ])) {
        Core.set(window, [ "JFix", "Sync" ], {});
}

JFix.HtmlLabel = Core.extend(Echo.Component, {

        $load : function() {
                Echo.ComponentFactory.registerType("JFixHtmlLabel", this);
        },

        componentType :"JFixHtmlLabel"
});

JFix.Sync.HtmlLabel = Core.extend(Echo.Render.ComponentSync, {

        $load : function() {
                Echo.Render.registerPeer("JFixHtmlLabel", this);
        },

        renderAdd : function(update, parentElement) {
                this.divElement = document.createElement("div");
                this.divElement.id = this.component.renderId;
                parentElement.appendChild(this.divElement);
                this.renderUpdate(update);
        },

        renderUpdate : function(update) {
                this.divElement.innerHTML = this.component.render("html", "");
                Echo.Sync.Font.render(this.component.render("font"), this.divElement);
                Echo.Sync.Color.renderFB(this.component, this.divElement);
                return false;
        },

        renderDispose : function(update) {
                delete this.divElement;
        }
});
