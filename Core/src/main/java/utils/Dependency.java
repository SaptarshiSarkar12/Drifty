package utils;

import properties.OS;
import properties.Program;

public enum Dependency {
    YT_DLP, DENO;

    public String getDownloadUrl(Dependency dependency) {
        boolean isWindows = OS.isWindows();
        boolean isMac = OS.isMac();
        boolean isArm = OS.isArm();
        boolean isX64 = OS.isX64();

        return switch (dependency) {
            case YT_DLP -> {
                if (isWindows) {
                    Program.setYtDlpExecutableName("yt-dlp.exe");
                    yield "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";
                } else if (isMac) {
                    Program.setYtDlpExecutableName("yt-dlp_macos");
                    yield "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_macos";
                } else {
                    Program.setYtDlpExecutableName("yt-dlp_linux");
                    yield "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp_linux";
                }
            }
            case DENO -> {
                if (isWindows) {
                    if (isArm) {
                        Program.setDenoExecutableName("deno-aarch64-pc-windows-msvc.exe");
                        yield "https://github.com/SaptarshiSarkar12/dino-exec-release/releases/latest/download/deno-aarch64-pc-windows-msvc.exe";
                    } else if (isX64) {
                        Program.setDenoExecutableName("deno-x86_64-pc-windows-msvc.exe");
                        yield "https://github.com/SaptarshiSarkar12/dino-exec-release/releases/latest/download/deno-x86_64-pc-windows-msvc.exe";
                    } else {
                        yield null;
                    }
                } else if (isMac) {
                    if (isArm) {
                        Program.setDenoExecutableName("deno-aarch64-apple-darwin");
                        yield "https://github.com/SaptarshiSarkar12/dino-exec-release/releases/latest/download/deno-aarch64-apple-darwin";
                    } else if (isX64) {
                        Program.setDenoExecutableName("deno-x86_64-apple-darwin");
                        yield "https://github.com/SaptarshiSarkar12/dino-exec-release/releases/latest/download/deno-x86_64-apple-darwin";
                    } else {
                        yield null;
                    }
                } else {
                    if (isArm) {
                        Program.setDenoExecutableName("deno-aarch64-unknown-linux-gnu");
                        yield "https://github.com/SaptarshiSarkar12/dino-exec-release/releases/latest/download/deno-aarch64-unknown-linux-gnu";
                    } else if (isX64) {
                        Program.setDenoExecutableName("deno-x86_64-unknown-linux-gnu");
                        yield "https://github.com/SaptarshiSarkar12/dino-exec-release/releases/latest/download/deno-x86_64-unknown-linux-gnu";
                    } else {
                        yield null;
                    }
                }
            }
        };
    }
}