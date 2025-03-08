"use client";
import { FaCodeBranch, FaJava, FaStar } from "react-icons/fa6";
import { GrLicense } from "react-icons/gr";
import { RiNextjsFill, RiTailwindCssFill } from "react-icons/ri";
import useSWRInfinite from "swr/infinite";
import Image from "next/image";
import { useEffect } from "react";
import useSWR from "swr";

interface Contributor {
  login: string;
  html_url: string;
  avatar_url: string;
  contributions: number;
  type: "User" | "Bot";
}

interface RepoData {
  forks_count: number;
  stargazers_count: number;
  owner: {
    login: string;
    avatar_url: string;
    html_url: string;
  };
}

const fetcher = (url: string) =>
  fetch(url).then((res) => {
    if (!res.ok) throw new Error("Failed to fetch data");
    return res.json();
  });

const getKey = (pageIndex: number, previousPageData: Contributor[]) => {
  if (previousPageData && !previousPageData.length) return null;
  return `https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors?per_page=100&page=${
    pageIndex + 1
  }`;
};

export default function AboutPage() {
  const { data: repoData, error: repoError } = useSWR<RepoData>(
    "https://api.github.com/repos/SaptarshiSarkar12/Drifty",
    fetcher
  );

  const {
    data: pages,
    error: contribError,
    isLoading,
    size,
    setSize,
  } = useSWRInfinite<Contributor[]>(getKey, fetcher, {
    dedupingInterval: 60000,
    revalidateOnFocus: false,
  });

  // Automatically load next page if there might be more data
  useEffect(() => {
    if (pages && pages[pages.length - 1]?.length === 100) {
      setSize(size + 1);
    }
  }, [pages, setSize, size]);

  // Flatten all pages into single array
  const allContributors = pages ? pages.flat() : [];

  // Filter and process contributors
  const humans = allContributors.filter((c) => c.type === "User");
  const bots = allContributors.filter((c) => c.type === "Bot");
  const maxVisible = 14;
  const visibleContributors = humans.slice(0, maxVisible);
  const otherContributorsCount =
    Math.max(humans.length - maxVisible, 0) + bots.length;

  // Error boundaries
  if (repoError)
    return (
      <div className="min-h-screen flex items-center justify-center">
        Failed to load repository data
      </div>
    );
  if (contribError)
    return (
      <div className="min-h-screen flex items-center justify-center">
        Failed to load contributors
      </div>
    );

  return (
    <div className="min-h-screen p-6 sm:p-8 ">
      <div className="max-w-6xl mx-auto space-y-12">
        {/* Header */}
        <header className="text-center space-y-6">
          <h1 className="text-5xl font-bold ">Drifty</h1>
          <p className="text-xl max-w-3xl mx-auto leading-relaxed">
            An open-source, multi-platform download utility designed with
            simplicity and efficiency in mind.
          </p>
        </header>

        {/* Stats Grid */}
        <div className="grid md:grid-cols-3 gap-6">
          <div className="p-6 rounded-xl shadow-md">
            <h3 className="text-sm font-medium mb-2">License</h3>
            <div className="flex items-center space-x-2">
              <div className="h-8 w-8 bg-green-100 dark:bg-green-900 rounded-lg flex items-center justify-center">
                <GrLicense className="text-green-600 dark:text-green-300" />
              </div>
              <a
                href="https://github.com/SaptarshiSarkar12/Drifty/blob/master/LICENSE"
                className="text-lg font-medium hover:underline text-gray-900 dark:text-white"
                aria-label="View Apache 2.0 License"
              >
                Apache 2.0
              </a>
            </div>
          </div>

          <div className="p-6 rounded-xl shadow-md">
            <h3 className="text-sm font-medium mb-2 ">Technology</h3>
            <div className="flex items-center space-x-2">
              <div className="h-8 w-8 bg-purple-100 dark:bg-purple-900 rounded-lg flex items-center justify-center">
                <FaJava className="text-purple-600 dark:text-purple-300" />
              </div>
              <span className="text-lg font-medium text-gray-900 dark:text-white">
                Java
              </span>
            </div>
          </div>

          <div className="p-6 rounded-xl shadow-md">
            <h3 className="text-sm font-medium mb-2 ">Website</h3>
            <div className="flex flex-wrap items-center gap-3">
              <div className="flex items-center space-x-2">
                <div className="h-8 w-8 bg-gray-200 dark:bg-gray-700 rounded-lg flex items-center justify-center">
                  <RiNextjsFill className="text-gray-800 dark:text-gray-200" />
                </div>
                <span className="text-gray-900 dark:text-white">Next.js</span>
              </div>
              <div className="flex items-center space-x-2">
                <div className="h-8 w-8 bg-blue-100 dark:bg-blue-900 rounded-lg flex items-center justify-center">
                  <RiTailwindCssFill className="text-blue-400 dark:text-blue-300" />
                </div>
                <span className="text-gray-900 dark:text-white">Tailwind</span>
              </div>
            </div>
          </div>
        </div>

        {/* Project Owner Section */}
        {repoData && (
          <div className="flex flex-col md:flex-row items-stretch gap-4 w-full">
            {/* Maintainer Card */}
            <section className="p-8 rounded-2xl shadow-md flex-1 flex items-center min-h-[200px]">
              <div className="flex items-center space-x-6 w-full">
                <Image
                  src={repoData.owner.avatar_url}
                  alt={`${repoData.owner.login}'s profile picture`}
                  className="w-24 h-24 rounded-full shadow-lg"
                  width={96}
                  height={96}
                  loading="lazy"
                />
                <div>
                  <h2 className="text-2xl font-bold">Saptarshi Sarkar</h2>
                  <p className="text-lg mt-1">Maintainer</p>
                  <a
                    href={repoData.owner.html_url}
                    className="inline-flex items-center hover:underline mt-2"
                    aria-label="View GitHub profile"
                  >
                    <span>@{repoData.owner.login}</span>
                    <svg
                      className="w-4 h-4 ml-2"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14"
                      />
                    </svg>
                  </a>
                </div>
              </div>
            </section>

            {/* Stats Card */}
            <section className="p-8 rounded-2xl shadow-md flex-1 flex items-center min-h-[200px]">
              <div className="flex items-center space-x-4 w-full justify-around">
                <div className="flex items-center space-x-2">
                  <div className="h-10 md:h-16 w-10 md:w-16 bg-gray-100 dark:bg-gray-800 rounded-lg flex items-center justify-center">
                    <FaStar
                      className="text-gray-600 dark:text-gray-100 md:text-3xl"
                      aria-label="Stars"
                      title="Stars"
                    />
                  </div>
                  <span className="text-lg font-medium md:text-3xl">
                    {repoData.stargazers_count}
                  </span>
                </div>
                <div className="flex items-center space-x-2">
                  <div className="h-10 md:h-16 w-10 md:w-16 bg-gray-100 dark:bg-gray-800 rounded-lg flex items-center justify-center">
                    <FaCodeBranch
                      className="text-gray-600 dark:text-gray-100 md:text-3xl"
                      aria-label="Forks"
                      title="Forks"
                    />
                  </div>

                  <span className="text-lg font-medium md:text-3xl">
                    {repoData.forks_count}
                  </span>
                </div>
              </div>
            </section>
          </div>
        )}

        {/* Contributors Section */}
        <section className="space-y-8">
          <div className="space-y-4">
            <h2 className="text-3xl font-bold text-gray-900 dark:text-white">
              Top Contributors
            </h2>
            <p className="max-w-2xl text-gray-600 dark:text-gray-300">
              Honouring the developers who contributed to the creation of
              Drifty.
            </p>
          </div>

          <div className="p-6 rounded-2xl shadow-md">
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
              {isLoading &&
                Array.from({ length: 15 }).map((_, i) => (
                  <div key={i} className="animate-pulse">
                    <div className="h-20 bg-gray-200 dark:bg-gray-700 rounded-xl" />
                  </div>
                ))}

              {!isLoading && visibleContributors.length === 0 && (
                <div className="col-span-full text-center py-8 text-gray-500">
                  No contributors found
                </div>
              )}

              {visibleContributors.map((contributor) => (
                <a
                  key={contributor.login}
                  href={contributor.html_url}
                  className="group flex flex-col items-center p-4 rounded-xl transition-colors"
                  aria-label={`View ${contributor.login}'s profile`}
                >
                  <Image
                    src={contributor.avatar_url}
                    alt={`${contributor.login}'s avatar`}
                    className="w-16 h-16 rounded-full shadow mb-3"
                    width={64}
                    height={64}
                    loading="lazy"
                  />
                  <span className="text-sm font-medium text-center group-hover:text-blue-600 dark:group-hover:text-blue-400">
                    {contributor.login}
                  </span>
                  <span className="text-xs  text-center">
                    {contributor.contributions} contributions
                  </span>
                </a>
              ))}

              {otherContributorsCount > 0 && (
                <a
                  href="https://github.com/SaptarshiSarkar12/Drifty/graphs/contributors"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex flex-col items-center justify-center p-4 rounded-xl transition-colors group"
                >
                  <div className="w-16 h-16 rounded-full bg-gray-200 dark:bg-gray-600 flex items-center justify-center text-xl font-bold mb-3">
                    +{otherContributorsCount}
                  </div>
                  <span className="text-sm text-center group-hover:text-blue-600 dark:group-hover:text-blue-400">
                    More contributors
                  </span>
                </a>
              )}
            </div>

            {isLoading && pages && (
              <div className="text-center py-4  mt-4">
                Loading more contributors...
              </div>
            )}
          </div>
        </section>
      </div>
    </div>
  );
}
