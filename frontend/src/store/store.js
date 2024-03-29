import { defineStore } from "pinia";

export const StoreConfig = defineStore("store", {
    state: () => ({
        colorPalette: {
            background: "#173d0eff",
            text: "#d8d86f",
            titleSize: 20,
            fontSize: 12,
        },
    }),
    mutations: {
        updateColorPalette(state, { background, text, titleSize, fontSize }) {
            state.colorPalette.background = background;
            state.colorPalette.text = text;
            state.colorPalette.titleSize = titleSize;
            state.colorPalette.fontSize = fontSize;
        },
    },
    actions: {
        setColorPalette(background, text, titleSize, fontSize) {
            this.colorPalette.background = background;
            this.colorPalette.text = text;
            this.colorPalette.titleSize = titleSize;
            this.colorPalette.fontSize = fontSize;
        },
    },
});
