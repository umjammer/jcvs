package com.ice.jcvsii;

public
class CVSThread
        extends Thread {
    protected Monitor monitor;
    protected Runnable subRunner;

    /**
     * We severely restrict construction!
     */
    private CVSThread() {
    }

    private CVSThread(String name) {
    }

    private CVSThread(Runnable runner) {
    }

    private CVSThread(ThreadGroup group, String name) {
    }

    private CVSThread(ThreadGroup group, Runnable runner, String name) {
    }

    public CVSThread(String name, Runnable runner, Monitor monitor) {
        super(name);
        this.monitor = monitor;
        this.subRunner = runner;
    }

    @Override
    public void
    run() {
        this.monitor.threadStarted();

        this.subRunner.run();

        this.monitor.threadFinished();
    }

    public
    interface Monitor {
        void
        threadStarted();

        void
        threadCanceled();

        void
        threadFinished();
    }

}
