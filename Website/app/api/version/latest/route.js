import { NextResponse } from "next/server";

export async function getLatestVersion() {
  let latestReleases = await fetch(
    "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases/latest",
    {
      next: { revalidate: 60 },
    },
  ).then((res) => res.json());
  return latestReleases.tag_name.replace("v", "");
}

export async function GET() {
  let version = await getLatestVersion();
  return new NextResponse(version);
}
