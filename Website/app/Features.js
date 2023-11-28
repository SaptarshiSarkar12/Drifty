import React from "react";

const Features = () => {
  const features = [
    {
      title: "It's Free and Open-Source",
      description:
        "Drifty is both free and open-source, so, you can bring new features that you wish to have 🎉",
      icon: "fa-brands fa-osi fa-xl",
      color: "text-lime-600",
      colorHex: "#65a30d",
    },
    {
      title: "Faster Downloading of files",
      description:
        "Drifty supports parallel downloading using multiple threads",
      icon: "fa-solid fa-download fa-xl",
      color: "text-sky-400",
      colorHex: "#38bdf8",
    },
    {
      title: "Support for downloading videos from YouTube, Instagram, etc.",
      description:
        "Drifty allows downloading videos from popular platforms like YouTube and Instagram with ease.",
      icon: "fa-brands fa-youtube fa-xl",
      colorHex: "#dc2626",
    },
    {
      title: "Available both in GUI and CLI mode",
      description:
        "Drifty has both CLI and GUI mode. So, it can be used anywhere as CLI or as GUI according to the requirements of the user.",
      icon: "fa-regular fa-display fa-xl",
      colorHex: "#374151",
    },
  ];

  return (
    <section className="p-5 xl:p-8 2xl:p-10 ">
      <div className="text-center text-4xl font-semibold">Features</div>
      <div className="grid grid-flow-row lg:grid-flow-col lg:[&>*:nth-of-type(1)]:row-span-2 lg:[&>*:nth-of-type(4)]:row-span-2 lg:items-center gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 grid-rows-4 sm:grid-rows-2 mt-5  mx-auto;">
        {features.map((feature, index) => (
          <div
            key={index}
            className="relative flex flex-col p-6 justify-center items-start text-neutral-400 bg-white shadow-xl shadow-slate-300 rounded-md overflow-hidden;"
          >
            <div
              className="absolute w-full h-[3px] top-0 left-0"
              style={{ backgroundColor: `${feature.colorHex}` }}
            ></div>
            <h2 className="text-xl font-semibold text-neutral-800">
              {feature.title}
            </h2>

            <div className="flex mt-2">
              <p className="text-sm text-center">{feature.description}</p>
              <div className={"text-6xl"}>
                <i
                  className={feature.icon}
                  style={{ color: `${feature.colorHex}` }}
                ></i>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default Features;
