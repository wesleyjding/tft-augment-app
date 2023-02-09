package main.enumeration;
import static main.enumeration.EnumerateWindows.Kernel32.*;
import static main.enumeration.EnumerateWindows.Psapi.*;
import static main.enumeration.EnumerateWindows.User32DLL.*;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

//Taken from https://stackoverflow.com/questions/6391439/getting-active-window-information-in-java/6393901#6393901
public class EnumerateWindows { // TODO: do this to get window in focus
    private static final int MAX_TITLE_LENGTH = 1024;
    public static String getActiveWindow() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        GetWindowTextW(GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
        System.out.println("Active window title: " + Native.toString(buffer));

        PointerByReference pointer = new PointerByReference();
        GetWindowThreadProcessId(GetForegroundWindow(), pointer);
        Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
        GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }


    static class Psapi {
        static { Native.register("psapi"); }
        public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
    }

    static class Kernel32 {
        static { Native.register("kernel32"); }
        public static int PROCESS_QUERY_INFORMATION = 0x0400;
        public static int PROCESS_VM_READ = 0x0010;
        public static native int GetLastError();
        public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
    }

    static class User32DLL {
        static { Native.register("user32"); }
        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
        public static native HWND GetForegroundWindow();
        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    }
}

