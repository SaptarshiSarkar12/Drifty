import React from "react";
import Image from "next/image";
import Osi from "./img/osi.svg";
import Download from "./img/download.svg";
import YouTube from "./img/youtube.svg";
import Desktop from "./img/desktop.svg";

const Features = () => {
  const features = [
    {
      id: 1,
      title: "It's Free and Open-Source",
      description:
          "Drifty is both free and open-source, so, you can bring new features that you wish to have 🎉",
      icon: Osi,
      color: "#65a30d",
    },
    {
      id: 2,
      title: "Faster Downloading of files",
      description:
          "Drifty supports parallel downloading using multiple threads",
      icon: Download,
      color: "#38bdf8",
    },
    {
      id: 3,
      title: "Support for downloading videos from YouTube, Instagram, etc.",
      description:
          "Drifty allows downloading videos from popular platforms like YouTube and Instagram with ease.",
      icon: YouTube,
      color: "#dc2626",
    },
    {
      id: 4,
      title: "Available both in GUI and CLI mode",
      description:
          "Drifty has both CLI and GUI mode. So, it can be used anywhere as CLI or as GUI according to the requirements of the user.",
      icon: Desktop,
      color: "#374151",
    },
  ];

  return (
      <section className="p-5">
        <div className="text-center text-4xl font-semibold">Features</div>
        <div className="grid grid-flow-row lg:grid-flow-col lg:[&>*:nth-of-type(1)]:row-span-2 lg:[&>*:nth-of-type(4)]:row-span-2 lg:items-center gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 grid-rows-4 sm:grid-rows-2 mt-5  mx-auto;">
          {features.map((card) => (
              <div
                  key={card.id}
                  className="relative flex flex-col p-6 justify-center items-start text-neutral-400 bg-white shadow-xl shadow-slate-300 rounded-md overflow-hidden;"
              >
                <div
                    className="absolute w-full h-[3px] top-0 left-0"
                    style={{ backgroundColor: `${card.color}` }}
                ></div>
                <h2 className="text-xl font-semibold text-neutral-800">
                  {card.title}
                </h2>

                <div className="flex mt-2">
                  <p className="text-sm leading-[24px] text-center">
                    {card.description}
                  </p>
                  <Image
                      src={card.icon}
                      alt="card logo"
                      width={90}
                      height={90}
                      style={{
                        position: "relative",
                      }}
                  />
                </div>
              </div>
          ))}
        </div>
      </section>
  );
};

export default Features;