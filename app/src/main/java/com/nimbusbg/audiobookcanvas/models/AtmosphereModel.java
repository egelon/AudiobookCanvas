package com.nimbusbg.audiobookcanvas.models;

enum Atmosphere
{
    Uplifting,
    Melancholic,
    Suspenseful,
    Whimsical,
    Serene,
    Foreboding,
    Nostalgic,
    Inspirational,
    Tense,
    Humorous,
    Enigmatic,
    Contemplative,
    Mystical,
    Somber,
    Energetic,
    Romantic,
    Satirical,
    Dreamy,
    Pensive,
    Ironic,
    Tranquil,
    Introspective,
    Uneasy,
    Eerie,
    Majestic,
    Lighthearted,
    Bittersweet,
    Soothing,
    Mournful,
    Optimistic,
    AweInspiring,
    Cynical,
    Invigorating,
    Gripping,
    Poignant,
    Intriguing,
    Reflective,
    Empowering,
    Bleak,
    Candid,
    Absorbing,
    Alluring,
    Ambivalent,
    Animated,
    Apprehensive,
    Celestial,
    Comforting,
    Dystopian,
    Ethereal,
    Fanciful,
    Futuristic,
    Idyllic,
    Imposing,
    Intense,
    Menacing,
    Provocative,
    Quirky,
    Resolute,
    Spirited,
    Wistful,
    Emotional
}

public class AtmosphereModel {
    Atmosphere primaryAtmosphere;
    Atmosphere secondaryAtmosphere;
    int leaningTowards;

    public AtmosphereModel(Atmosphere primaryAtmosphere, Atmosphere secondaryAtmosphere, int leaningTowards)
    {
        this.primaryAtmosphere = primaryAtmosphere;
        this.secondaryAtmosphere = secondaryAtmosphere;
        this.leaningTowards = leaningTowards;
    }

    public Atmosphere getPrimaryAtmosphere() {
        return primaryAtmosphere;
    }

    public void setPrimaryAtmosphere(Atmosphere primaryAtmosphere) {
        this.primaryAtmosphere = primaryAtmosphere;
    }

    public Atmosphere getSecondaryAtmosphere() {
        return secondaryAtmosphere;
    }

    public void setSecondaryAtmosphere(Atmosphere secondaryAtmosphere) {
        this.secondaryAtmosphere = secondaryAtmosphere;
    }

    public int getLeaningTowards() {
        return leaningTowards;
    }

    public void setLeaningTowards(int leaningTowards) {
        this.leaningTowards = leaningTowards;
    }
}
