import chroma from 'chroma-js';

export const getContrastColor = (color: string) => {
    const colorValue = chroma(color);
    return colorValue.luminance() > 0.5 ? 'black' : 'white';
};
