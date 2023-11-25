import React from "react";
function Features() {
  const features = [
    {
      title: "It's Free and Open-Source",
      description:
        "Drifty is both free and open-source, so, you can bring new features that you wish to have 🎉",
      icon: "fa-brands fa-osi",
      color: "text-lime-600",
    },
    {
      title: "Faster Downloading of files",
      description:
        "Drifty supports parallel downloading using multiple threads",
      icon: "fa fa-download",
      color: "text-sky-400",
    },
    {
      title: "Support for downloading videos from YouTube, Instagram, etc.",
      description:
        "Drifty allows downloading videos from popular platforms like YouTube and Instagram with ease.",
      icon: "fa fa-youtube-play",
      color: "text-red-600",
    },
    {
      title: "Available both in GUI and CLI mode",
      description:
        "Drifty has both CLI and GUI mode. So, it can be used anywhere as CLI or as GUI according to the requirements of the user.",
      icon: "fa fa-desktop",
      color: "text-gray-700",
    },
  ];

  return (
    <div className="py-6 px-12 background bg-gradient-to-r from-cyan-500 to-blue-500 text-white font-sans select-none">
      <h1 className="text-center mb-6 font-extrabold text-4xl pt-6">
        Features
      </h1>
      <div className="padding-x padding-y max-width">
        <section>
          <div className="grid grid-flow-row lg:grid-flow-col lg:[&>*:nth-of-type(1)]:row-span-2 lg:[&>*:nth-of-type(4)]:row-span-2 lg:items-center gap-6 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 grid-rows-4 sm:grid-rows-2 mt-16 max-w-[1110px] mx-auto;">
            {features.map((card) => (
              <div className="relative flex flex-col p-6 justify-center items-start text-neutral-400 bg-white shadow-xl shadow-slate-300 rounded-md overflow-hidden;">
                <div
                  className="absolute w-full h-[3px] top-0 left-0"
                  style={{ backgroundColor: `${card.color}` }}
                ></div>
                <h2 className="font-semibold text-neutral-800">{card.title}</h2>
                <p className="mt-1 text-sm leading-[22px]">
                  {card.description}
                </p>
                <div className="relative mt-8 w-14 lg:w-16 h-14 lg:h-16 self-end">
                  <Image
                    src={card.icon}
                    alt="card logo"
                    fill
                    priority
                    className="object-contain"
                  />
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
export default Features;
