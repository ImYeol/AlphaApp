package thealphalabs.Interface;

/**
 * ServiceControllerInterface
 *  Manage services such as bluetooth, wifi direct, and notification.
 *
 * @author Sukbeom Kim
 */
public interface ServiceControllerInterface {
    void start();
    void stop();
    void resume();
    void pause();
    void ready();       // 서비스의 기본 상태(실제로 사용되기 전)를 정의

    boolean isEnabled();
}
