"use client";

import { Tab } from "@headlessui/react";

function classNames(...classes) {
  return classes.filter(Boolean).join(" ");
}

export default function Demo() {
  const tabs = ["Drifty CLI", "Drifty GUI"];
  return (
    <div id="demo" className="bg-bottom flex flex-col gap-4">
      <h1 className="select-none text-center font-extrabold text-4xl pt-6">
        Demonstration of Drifty
      </h1>
      <p className="select-none text-center text-black text-2xl">
        Here is a quick demo of Drifty
      </p>
      <div className={"text-center select-none"}>
        <Tab.Group>
          <Tab.List className={"space-x-3"}>
            {tabs.map((tab) => (
              <Tab
                className={({ selected }) =>
                  classNames(
                    "w-36 rounded-full py-2.5 font-extrabold text-blue-700",
                    "ring-blue ring-opacity-60 ring-offset-2 ring-offset-bottom focus:outline-none focus:ring-2",
                    "hover:bg-[#004f6a]  duration-200",
                    selected
                      ? "text-white bg-blue-600 shadow"
                      : "hover:bg-[#3741514f]",
                  )
                }
                key={tab}
              >
                {tab}
              </Tab>
            ))}
          </Tab.List>
          <Tab.Panels>
            <Tab.Panel
              className={classNames(
                "rounded-xl bg-bottom p-3",
                "focus:outline-none focus:ring-0",
              )}
            >
              <div className="flex justify-center">
                <video
                  width="90%"
                  muted
                  loop
                  poster={"Video Thumbnails/CLI Thumbnail.jpg"}
                  controls
                  className={"rounded-lg"}
                >
                  <source
                    src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty@master/Website/public/Usages/CLI.webm"
                    type="video/webm"
                  />
                  <source
                    src={
                      "https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty/Website/public/Usages/CLI.mp4"
                    }
                    type="video/mp4"
                  />
                  Your browser does not support the video tag. Please watch the
                  video{" "}
                  <a
                    href={
                      "https://github.com/SaptarshiSarkar12/Drifty/blob/master/Website/public/Usages/CLI.mp4"
                    }
                  >
                    here
                  </a>
                  .
                </video>
              </div>
            </Tab.Panel>
            <Tab.Panel
              className={classNames(
                "rounded-xl bg-bottom p-3",
                "focus:outline-none focus:ring-0",
              )}
            >
              <div className="flex justify-center">
                <video
                  width="90%"
                  muted
                  loop
                  poster={"Video Thumbnails/GUI Thumbnail.jpg"}
                  controls
                  className={"rounded-lg"}
                >
                  <source
                    src="https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty/Website/public/Usages/GUI.webm"
                    type="video/webm"
                  />
                  <source
                    src={
                      "https://cdn.jsdelivr.net/gh/SaptarshiSarkar12/Drifty/Website/public/Usages/GUI.mp4"
                    }
                    type="video/mp4"
                  />
                  Your browser does not support the video tag. Please watch the
                  video{" "}
                  <a
                    href={
                      "https://github.com/SaptarshiSarkar12/Drifty/blob/master/Website/public/Usages/GUI.mp4"
                    }
                  >
                    here
                  </a>
                  .
                </video>
              </div>
            </Tab.Panel>
          </Tab.Panels>
        </Tab.Group>
      </div>
    </div>
  );
}
