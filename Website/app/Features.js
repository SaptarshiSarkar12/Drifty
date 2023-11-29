import React from "react";

const Features = () => {
  const features = [
    {
      title: "It's Free and Open-Source",
      description:
        "Drifty — a free and Open-Source tool, is open for suggestions. Share the features you want to have and help us improve",
      icon: "fa-brands fa-osi fa-xl",
      color: "text-lime-600",
      colorHex: "#65a30d",
    },
    {
      title: "Faster Downloading of files",
      description:
        "Drifty is designed to leverage concurrent threads for efficient parallel downloading with accelerated speeds",
      icon: "fa-solid fa-download fa-xl",
      color: "text-sky-400",
      colorHex: "#38bdf8",
    },
    {
      title: "Support for downloading videos from YouTube, Instagram, etc.",
      description:
        "Drifty provides an efficient solution to effortlessly download videos from leading platforms, such as YouTube and Instagram",
      icon: "fa-brands fa-youtube fa-xl",
      colorHex: "#dc2626",
    },
    {
      title: "Available both in GUI and CLI mode",
      description:
        "Drifty's CLI and GUI modes provide adaptable, streamlined user experiences across diverse scenarios",
      icon: "fa-solid fa-display fa-xl",
      colorHex: "#374151",
    },
  ];

  return (
    <section className="p-5 xl:p-8 2xl:p-10">
      <div className="text-center text-4xl font-semibold">Features</div>
      <div className="grid grid-flow-row lg:grid-flow-col lg:[&>*:nth-of-type(1)]:row-span-2 lg:[&>*:nth-of-type(4)]:row-span-2 lg:items-center gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 grid-rows-4 sm:grid-rows-2 mt-5  mx-auto;">
        {features.map((feature, index) => (
          <div
            key={feature.title}
            className="relative flex flex-col p-6 justify-center items-start text-neutral-400 bg-white shadow-2xl shadow-slate-400 rounded-lg overflow-hidden"
          >
            <div
              className="absolute w-full h-[3px] top-0 left-0"
              style={{ backgroundColor: `${feature.colorHex}` }}
            ></div>
            <h2 className="text-xl font-semibold text-neutral-800">
              {feature.title}
            </h2>

            <div className="flex mt-2">
              <p className="text-sm text-center text-gray-600">{feature.description}</p>
              <div className={"text-6xl p-2"}>
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
