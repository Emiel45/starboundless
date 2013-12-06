#include <windows.h>
#include <stdio.h>

int main(int argc, char *argv[]) {
    STARTUPINFOA startupInfo;
    PROCESS_INFORMATION processInformation;

    ZeroMemory(&startupInfo, sizeof(startupInfo));
    ZeroMemory(&processInformation, sizeof(processInformation));

    startupInfo.cb = sizeof(startupInfo);

    if(argc != 2) {
        printf("Usage: launcher.exe <path_to_starbound.exe>");
        return 1;
    }

    CreateProcessA(argv[1], 0, 0, 0, FALSE,  CREATE_SUSPENDED, 0, 0, &startupInfo, &processInformation);

    FARPROC loadLibraryAddress = GetProcAddress(GetModuleHandleA("kernel32"), "LoadLibraryA");

    char fullPath[MAX_PATH];
    GetFullPathNameA("loader.dll", sizeof(fullPath), fullPath, NULL);

    HANDLE processHandle = processInformation.hProcess;
    LPVOID remotePath = VirtualAllocEx(processHandle, NULL, MAX_PATH, MEM_RESERVE | MEM_COMMIT, PAGE_READWRITE);

    SIZE_T writtenBytes;
    WriteProcessMemory(processHandle, remotePath, fullPath, MAX_PATH, &writtenBytes);

    HANDLE remoteThread = CreateRemoteThread(processHandle, NULL, 0, (LPTHREAD_START_ROUTINE) loadLibraryAddress, remotePath, 0, NULL);
    return 0;
}