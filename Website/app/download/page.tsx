"use client";

import { useEffect, useState } from "react";
import { FaDownload, FaInfoCircle, FaGithubAlt } from "react-icons/fa";
import { FaApple, FaLinux, FaWindows } from "react-icons/fa6";
import { FiGithub } from "react-icons/fi";

// Define types for GitHub API response
type ReleaseAsset = {
  name: string;
  browser_download_url: string;
  download_count?: number;
};

type Release = {
  id: number;
  tag_name: string;
  name: string;
  assets: ReleaseAsset[];
  body: string;
  html_url: string;
  published_at: string;
  prerelease: boolean;
};

export default function DownloadPage() {
  const [releases, setReleases] = useState<Release[]>([]);
  const [latestRelease, setLatestRelease] = useState<Release | null>(null);
  const [selectedRelease, setSelectedRelease] = useState<Release | null>(null);
  const [totalDownloads, setTotalDownloads] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>("");

  useEffect(() => {
    const fetchReleases = async () => {
      try {
        const response = await fetch(
          "https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases"
        );
        if (!response.ok) throw new Error("Failed to fetch releases");
        const data: Release[] = await response.json();
        if (Array.isArray(data) && data.length > 0) {
          console.log(data);
          console.debug(latestRelease);
          setReleases(data);
          setLatestRelease(data[0]);
          setSelectedRelease(data[0]); // Default to the latest release
          const total = data.reduce(
            (sum, release) =>
              sum +
              release.assets.reduce(
                (aSum, asset) => aSum + (asset.download_count || 0),
                0
              ),
            0
          );
          setTotalDownloads(total);
        }
      } catch (error) {
        console.error("Error fetching releases:", error);
        setError("Failed to load releases. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchReleases();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleVersionSelect = (tag_name: string) => {
    const release = releases.find((r) => r.tag_name === tag_name) || null;
    setSelectedRelease(release);
  };

  const filteredAssets = selectedRelease
    ? selectedRelease.assets.filter((asset) =>
        asset.name.toLowerCase().includes(searchQuery.toLowerCase())
      )
    : [];

  if (loading) {
    return (
      <div className="flex justify-center items-center h-full">
        <div className="text-center flex justify-center items-center flex-col">
          <FaGithubAlt className="animate-bounce text-4xl" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex justify-center items-center">
        <div className="text-center">
          <p className="text-lg text-(--button-bg)">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="mt-4 bg-(--button-bg) text-(--button-text) px-4 py-2 rounded-lg transition duration-200 hover:scale-105"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col items-center p-6 w-full">
      {/* Header Section */}
      <div className="text-center mb-8">
        <h1 className="text-4xl font-bold">Download Drifty</h1>
        <p className="mt-1 text-lg text-gray-600 dark:text-gray-300">
          Fast, secure, and effortless file downloads.
        </p>
        <span className="mt-2 inline-block bg-(--button-bg) text-(--button-text)  px-4 py-2 rounded-full text-sm font-semibold">
          Total Downloads: {totalDownloads.toLocaleString()}
        </span>
      </div>

      {/* Version Selector */}
      {releases.length > 1 && (
        <div className="w-full max-w-md mb-6">
          <label className="block text-lg font-medium mb-2">
            Select Version:
          </label>
          <select
            className="w-full p-3  dark:bg-(--dark-bg) border border-gray-300 dark:border-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-(--button-bg)"
            onChange={(e) => handleVersionSelect(e.target.value)}
            value={selectedRelease?.tag_name || ""}
          >
            <option value="" disabled>
              -- Select a Version --
            </option>
            {releases.map((release) => (
              <option key={release.id} value={release.tag_name}>
                {release.tag_name} (
                {new Date(release.published_at).toLocaleDateString()})
              </option>
            ))}
          </select>
        </div>
      )}

      {/* Release Details */}
      {selectedRelease && (
        <div className=" shadow-lg rounded-lg p-6 w-full max-w-5xl mb-6">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-semibold">
              {selectedRelease.name}
              {(selectedRelease === releases[0] ||
                selectedRelease.prerelease) && (
                <span
                  className={`inline-flex items-center px-2 pt-0.2 pb-0.5 mx-1 rounded-full text-xs font-medium border ${
                    selectedRelease === releases[0]
                      ? "border-green-500 text-green-500 animate-pulse"
                      : "border-yellow-500 text-yellow-500 "
                  }`}
                >
                  {selectedRelease === releases[0] ? "Latest" : "Pre-Release"}
                </span>
              )}
            </h2>
            <span className=" px-3 py-1 rounded-full text-sm font-semibold">
              {selectedRelease.assets
                .reduce((sum, asset) => sum + (asset.download_count || 0), 0)
                .toLocaleString()}{" "}
              Downloads
            </span>
          </div>
          {releases.length > 0 && (
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">
              Released on{" "}
              {new Date(selectedRelease.published_at).toLocaleDateString()}
            </p>
          )}

          {/* Search Bar */}
          <div className="mt-4">
            <input
              type="text"
              placeholder="Search assets..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full p-3 dark:bg-(--dark-bg) border border-gray-300 dark:border-gray-700 rounded-md focus:outline-none focus:ring-2 focus:ring-(--button-bg)"
            />
          </div>

          {/* Asset List */}
          <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-6">
            {filteredAssets.length > 0 ? (
              filteredAssets.map((asset) => (
                <a
                  key={asset.name}
                  href={asset.browser_download_url}
                  className="block  px-4 py-2 rounded-lg shadow-md  transition duration-200"
                >
                  <div className="flex justify-between items-center">
                    <span>{asset.name}</span>
                    <div className="flex items-center space-x-2">
                      {(asset.name.toLowerCase().includes("linux") ||
                        asset.name.toLowerCase().includes(".pkg")) && (
                        <FaLinux className="text-black dark:text-white h-6 w-6" />
                      )}
                      {(asset.name.toLowerCase().includes("mac") ||
                        asset.name.toLowerCase().includes("dmg")) && (
                        <FaApple className="text-gray-500 dark:text-gray-300 h-6 w-6" />
                      )}
                      {(asset.name.toLowerCase().endsWith(".exe") ||
                        asset.name.toLowerCase().endsWith(".msi")) && (
                        <FaWindows className="text-blue-400 h-6 w-6" />
                      )}{" "}
                      <FaDownload />
                    </div>
                  </div>
                </a>
              ))
            ) : (
              <p className="text-gray-500 dark:text-gray-400 col-span-full">
                No assets found matching your search.
              </p>
            )}
          </div>

          {/* Release Notes Link */}
          <div className="mt-6 text-sm ">
            <a
              href={selectedRelease.html_url}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center space-x-1  hover:underline"
            >
              <FaInfoCircle />
              <span>View Release Notes</span>
            </a>
          </div>
        </div>
      )}

      {/* GitHub Link */}
      <div className="mt-12 text-center">
        <a
          href="https://github.com/SaptarshiSarkar12/Drifty"
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex items-center space-x-3 bg-(--button-bg) text-(--button-text) px-6 py-3 rounded-lg shadow-md hover:scale-105 transition duration-200"
        >
          <FiGithub className="text-2xl" />
          <span className="text-lg font-medium">View on GitHub</span>
        </a>
      </div>
    </div>
  );
}
