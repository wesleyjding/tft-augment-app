package main.augmentStatGenerator;

public class AugmentStatGeneratorObserver {
    private int cacheProgress;

    public AugmentStatGeneratorObserver() {
        cacheProgress = 0;
    }

    public int getCacheProgress() {
        return cacheProgress;
    }

    public void setCacheProgress(int cacheProgress) {
        this.cacheProgress = cacheProgress;
    }

}
