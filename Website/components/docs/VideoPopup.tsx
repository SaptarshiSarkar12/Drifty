"use client";

import { useState } from "react";
import { Cards } from "nextra/components";

type VideoPopupProps = {
  videoUrl: string;
  buttonText?: string;
};

export default function VideoPopup({
  videoUrl,
  buttonText = "Watch Demo",
}: VideoPopupProps) {
  const [open, setOpen] = useState(false);

  return (
    <div className="flex justify-center">
      <Cards>
        <button
          className="px-4 py-2 bg-(--button-bg) text-(--button-text) rounded-md shadow-md hover:bg-(--button-text) hover:text-(--button-bg) transition"
          onClick={() => setOpen(true)}
        >
          {buttonText}
        </button>
      </Cards>

      <div
        className={`fixed inset-0 flex items-center justify-center bg-black/80 p-4 transition-opacity duration-100 ${
          open
            ? "opacity-100 scale-100"
            : "opacity-0 scale-90 pointer-events-none"
        }`}
        aria-hidden={!open}
      >
        <div className="relative w-full sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-3xl bg-white rounded-xl shadow-lg transform transition-all">
          <div className="flex items-center justify-between p-4 border-b border-gray-200">
            <h3 className="text-lg font-semibold text-gray-900">
              {buttonText}
            </h3>
            <button
              className="text-gray-500 hover:bg-gray-200 rounded-full p-3 w-12 h-12 focus:outline-none focus:ring-2 focus:ring-gray-400"
              onClick={() => setOpen(false)}
            >
              ✕
            </button>
          </div>
          <div className="p-4">
            <video
              className="w-full max-h-[40vh] rounded-lg shadow-md"
              controls
            >
              <source src={videoUrl} type="video/mp4" />
              Your browser does not support the video tag.
              <a href={videoUrl}>Click here</a> to play the video.
            </video>
          </div>
        </div>
      </div>
    </div>
  );
}
