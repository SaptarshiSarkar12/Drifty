"use client";

import Link from "next/link";

export default function Hero() {
  return (
    <section
      id="hero"
      className="relative flex flex-col items-center justify-center text-center w-full h-[70vh] px-6"
    >
      <h1 className="text-4xl md:text-7xl font-bold tracking-tight leading-tight">
        Drifty: Speed, Simplicity, and Open-Source
      </h1>

      <p className="mt-4 text-lg md:text-xl opacity-80 max-w-3xl">
        Download files like never before.
      </p>

      {/* CTA Buttons */}
      <div className="mt-6 flex flex-col sm:flex-row sm:justify-center space-y-3 sm:space-y-0 sm:space-x-4">
        <Link href="/download">
          <button className="group w-full sm:w-auto px-8 py-3 text-lg font-medium rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-current transform hover:bg-current active:scale-95">
            <span className="dark:group-hover:text-black group-hover:text-white">
              Get Started
            </span>
          </button>
        </Link>
        <a href="https://github.com/SaptarshiSarkar12/Drifty" target="_blank">
          <button className="group w-full sm:w-auto px-8 py-3 text-lg font-medium rounded-lg shadow-md hover:shadow-lg transition-all duration-200 border border-current transform hover:bg-current active:scale-95">
            <span className="dark:group-hover:text-black group-hover:text-white">
              View on GitHub
            </span>
          </button>
        </a>
      </div>
    </section>
  );
}
