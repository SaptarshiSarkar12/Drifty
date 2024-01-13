import {NextResponse} from "next/server";

export async function getDevelopmentVersion(){
    let devVersionJson = (await fetch("https://raw.githubusercontent.com/SaptarshiSarkar12/Drifty/master/version.json", {
        next: {revalidate: 60}
    })).text();
    return JSON.parse(await devVersionJson).version;
}

export async function GET() {
    let version = await getDevelopmentVersion();
    return new NextResponse(version)
}