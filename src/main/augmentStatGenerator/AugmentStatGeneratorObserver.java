package main.augmentStatGenerator;

public class AugmentStatGeneratorObserver {
    private int cacheProgress;
    private boolean interrupt;

    public AugmentStatGeneratorObserver() {
        cacheProgress = 0;
        this.interrupt = false;
    }

    public int getCacheProgress() {
        return cacheProgress;
    }

    public void setCacheProgress(int cacheProgress) {
        this.cacheProgress = cacheProgress;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    public void setInterrupt(boolean interrupt) {
        this.interrupt = interrupt;
    }
}
