import React, { useEffect, useState } from "react";

const ReleaseInfo = () => {
  const [releases, setReleases] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    let isMounted = true;

    const fetchReleases = async () => {
      try {
        const response = await fetch(
          "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases",
        );
        if (response.ok && isMounted) {
          const data = await response.json();
          setReleases(data);
          setError(null); // Clear any previous error on successful fetch
        } else {
          throw new Error("Failed to fetch releases");
        }
      } catch (error) {
        console.error("Error fetching data:", error);
        if (isMounted) {
          setError(
            "Failed to load releases. Please visit https://github.com/SaptarshiSarkar12/Drifty/releases.",
          );
        }
      }
    };

    fetchReleases();

    return () => {
      isMounted = false;
    };
  }, []);

  const latestRelease = releases.reduce((latest, release) => {
    if (
      (latest === null && !release.prerelease) ||
      (latest !== null &&
        new Date(release.published_at) > new Date(latest.published_at) &&
        !release.prerelease)
    ) {
      return release;
    }
    return latest;
  }, null);

  return (
    <div>
      {error && <p className="text-red-500">{error}</p>}{" "}
      {/* Display error message */}
      {!error && releases.length === 0 && <p>Loading releases...</p>}{" "}
      {/* Show loading message */}
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
              ) : release === latestRelease ? (
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
