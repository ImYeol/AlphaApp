package thealphalabs.controller;

import android.support.v4.util.ArrayMap;

import thealphalabs.Interface.ServiceControllerInterface;

/**
 * ServiceController
 *  어플리케이션에서 사용되는 모든 서비스를 제어한다.
 *  해당 클래스는 싱글톤으로 구현된다. 모든 필요한 메서드는 static으로 구현되기
 *  때문에 어플리케이션에서 객체가 생성되어 있다는 것을 전제하고 사용되어야 한다.
 *
 * @author Sukbeom Kim
 */
public class ServiceController {
    /**
     * @desc    객체가 있는 경우에는 기존에 있던 객체를 리턴하고 없는 경우에는
     *          새로운 객체를 생성하여 리턴한다.
     *
     * @return  ServiceController 객체
     */
    public  static ServiceController getInstance()
    {
        if (instance == null) {
            instance = new ServiceController();
        }
        return instance;
    };
    private static ServiceController instance;

    /**
     * Service 에 대한 static 변수 정의
     */
    public static String   BLUETOOTH_SERVICE    = "BLUETOOTH";
    public static String   WIFIDIRECT_SERVICE   = "WIFIDIRECT";
    public static String   ACCLSENSOR_SERVICE   = "ACCLSENSOR";
    public static String   GYROSENSOR_SERVICE   = "GYROSENSOR";
    public static String   NOTIFICATION_SERVICE = "NOTIFICATION";

    ArrayMap<String, ServiceControllerInterface> services;
    ServiceController() {
        // 기본 멤버변수들을 초기화한다.
        services    = new ArrayMap<>();
    }

    /**
     * 원하는 서비스 등록
     */
    public static void registerService(String key, ServiceControllerInterface serviceController)
    {
        getInstance().services.put(key, serviceController);
    }

    /**
     * 서비스 등록 해제
     */
    public static void unregisterService(String key)
    {
        getInstance().services.remove(key);
    }

    /**
     * 서비스 실행 여부에 따라 서비스 실행
     */
    public static void controlService(boolean start, String service) {
        if (start) {
            startService(service);
        } else {
            stopService(service);
        }
    }

    /**
     * 등록된 모든 서비스를 실행한다.
     */
    public static void startAllServices() {
        ArrayMap<String, ServiceControllerInterface> services = getInstance().services;
        for (int i = 0; i < services.size(); i++) {
            String key = services.keyAt(i);
            services.get(key).start();
        }
    }

    public static void startService(String key) {
        getInstance().services.get(key).start();
    }

    /**
     * 등록된 모든 서비스를 멈춘다.
     */
    public static void pauseAllServices() {
        ArrayMap<String, ServiceControllerInterface> services = getInstance().services;
        for (int i = 0; i < services.size(); i++) {
            String key = services.keyAt(i);
            services.get(key).pause();
        }
    }

    public static void pauseService(String key) {
        getInstance().services.get(key).pause();
    }

    /**
     * 등록된 모든 서비스를 종료한다.
     */
    public static void stopAllServices() {
        ArrayMap<String, ServiceControllerInterface> services = getInstance().services;
        for (int i = 0; i < services.size(); i++) {
            String key = services.keyAt(i);
            services.get(key).stop();
        }
    }

    public static void stopService(String key) {
        getInstance().services.get(key).stop();
    }

    /**
     * 등록된 모든 서비스를 재개한다.
     */
    public static void resumeAllServices() {
        ArrayMap<String, ServiceControllerInterface> services = getInstance().services;
        for (int i = 0; i < services.size(); i++) {
            String key = services.keyAt(i);
            services.get(key).resume();
        }
    }

    public static void resumeService(String key) {
        getInstance().services.get(key).resume();
    }


    /**
     * 등록된 모든 서비스를 준비한다.
     */
    public static void readyAllServices() {
        ArrayMap<String, ServiceControllerInterface> services = getInstance().services;
        for (int i = 0; i < services.size(); i++) {
            String key = services.keyAt(i);
            services.get(key).ready();
        }
    }

    public static void readyService(String key) {
        getInstance().services.get(key).ready();
    }

    /**
     * 등록된 서비스에 대한 정보를 얻어온다.
     */
    public static ServiceControllerInterface getService(String key) {
        return getInstance().services.get(key);
    }

    public static boolean isServiceEnabled(String serviceName) {
        return getInstance().services.get(serviceName).isEnabled();
    }
}
