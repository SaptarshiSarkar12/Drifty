import React, { useEffect, useState } from "react";

const ReleaseInfo = () => {
  const [releases, setReleases] = useState([]);

  useEffect(() => {
    const fetchReleases = async () => {
      try {
        const response = await fetch(
          "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases",
        );
        if (response.ok) {
          const data = await response.json();
          setReleases(data);
        } else {
          throw new Error("Failed to fetch releases");
        }
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchReleases();
  }, []);

  let latestRelease = null;

  for (let i = 0; i < releases.length; i++) {
    const release = releases[i];

    // Skip over the first release if it's a prerelease
    if (i === 0 && release.prerelease) {
      continue;
    }

    if (
      !latestRelease ||
      (new Date(release.published_at) > new Date(latestRelease.published_at) &&
        !release.prerelease)
    ) {
      latestRelease = release;
    }
  }

  return (
    <div>
      {releases.map((release) => (
        <div
          key={release.id}
          className="flex justify-between items-center pb-4 pt-2"
        >
          <div>
            <h2 className="text-lg font-semibold">
              {release.name}{" "}
              {release.prerelease ? (
                <span className="text-xs text-yellow-500 border border-yellow-500 rounded-full p-1">
                  Pre Release
                </span>
              ) : release === latestRelease ? ( // Check if it's the latest non-prerelease release
                <span className="text-xs text-green-500 border border-green-500 rounded-full p-1 motion-safe:animate-pulse">
                  Latest
                </span>
              ) : null}
            </h2>
            <p className="text-gray-500 text-sm">
              Version : {release.tag_name}
            </p>
            <p className="text-gray-300 text-sm">
              Release : {new Date(release.published_at).toLocaleString()}
            </p>
          </div>
          <a
            href={release.html_url}
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-500 hover:underline"
          >
            Changelog
          </a>
        </div>
      ))}
    </div>
  );
};

export { ReleaseInfo };
