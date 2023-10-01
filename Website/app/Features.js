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
      <div className="column-1 flex flex-wrap justify-center gap-1">
        {features.map((feature, index) => (
          <div
            key={index}
            className="m-2 p-4 rounded-md h-max flex flex-col justify-center shadow-lg bg-slate-100 font-bold text-black w-60 hover:-translate-y-1 hover:scale-110 duration-300"
          >
            <i
              className={feature.icon + " self-center mb-5 " + feature.color}
              aria-hidden="true"
              style={{ fontSize: "3rem" }}
            ></i>
            <h3 className="text-lg mb-1">{feature.title}</h3>
            <p className="font-normal leading-normal">{feature.description}</p>
          </div>
        ))}
      </div>
      <div className="text-2xl font-bold flex justify-center my-4">
        ....and many more!
      </div>
    </div>
  );
}
export default Features;
