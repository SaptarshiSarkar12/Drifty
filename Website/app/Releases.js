"use client";

import { useEffect, useMemo, useState } from "react";
import { remark } from "remark";
import html from "remark-html";

function downloadLatestRelease(OSName, applicationType) {
  if (applicationType === "CLI") {
    if (OSName === "Windows exe") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-CLI.exe"
      );
    } else if (OSName === "Linux") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-CLI_linux"
      );
    } else {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-CLI_macos"
      );
    }
  } else {
    if (OSName === "Windows exe") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI.exe"
      );
    } else if (OSName === "Windows msi") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI.msi"
      );
    } else if (OSName === "Linux") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI_linux"
      );
    } else {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/latest/download/Drifty-GUI.pkg"
      );
    }
  }
}

function downloadOlderReleases(OSName, applicationType, version) {
  if (applicationType === "CLI") {
    if (OSName === "Windows") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-CLI.exe"
      );
    } else if (OSName === "Linux") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-CLI_linux"
      );
    } else {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-CLI_macos"
      );
    }
  } else {
    if (OSName === "Windows exe") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-GUI.exe"
      );
    } else if (OSName === "Windows msi") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-GUI.msi"
      );
    } else if (OSName === "Linux") {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-GUI_linux"
      );
    } else {
      window.open(
        "https://github.com/SaptarshiSarkar12/Drifty/releases/download/" +
          version +
          "/Drifty-GUI.pkg"
      );
    }
  }
}

export default function Releases({ props }) {
  const [buttonStates, setButtonStates] = useState({});
  const [content, setContent] = useState([]);
  const [applicationType, setApplicationType] = useState("GUI");
  const maxReleasesToDisplay = 3;
  const filteredReleases = useMemo(() => {
    const releases = [];
    props.release.map((item, index) => {
      if (index !== 0) {
        if (
          item.tag_name.startsWith("v2") &&
          releases.length <= maxReleasesToDisplay
        ) {
          releases.push(item);
        }
      }
    });
    return releases;
  }, [props.release]);
  const filterOlderReleases = useMemo(() => {
    const releases = [];
    let noOfReleases = filteredReleases.length;
    props.release.map((item, index) => {
      if (
        releases.length <= maxReleasesToDisplay &&
        noOfReleases < maxReleasesToDisplay &&
        index !== 0
      ) {
        if (!item.tag_name.startsWith("v2")) {
          releases.push(item);
          noOfReleases++;
        }
      }
    });
    return releases;
  }, [filteredReleases.length, props.release]);

  const handleButtonClick = (index) => {
    setButtonStates((prevState) => ({
      ...prevState,
      [index]: !prevState[index],
    }));
  };
  const markerToHtml = async (itemBody, i) => {
    const cont = await remark().use(html).process(itemBody);
    setContent((prev) => {
      prev[i] = cont.toString();
      return prev;
    });
  };
  useEffect(() => {
    filteredReleases.forEach(async (item, index) => {
      await markerToHtml(item.body, index);
    });
    filterOlderReleases.forEach(async (item, index) => {
      await markerToHtml(item.body, index + filteredReleases.length);
    });
  }, [filteredReleases, filterOlderReleases]);

  const handleApplicationTypeChange = (applicationType) => {
    setApplicationType(applicationType.target.value);
  };

  return (
    <div
      id="download"
      className="bg-gradient-to-b from-top from-8% via-cyan-300 to-bottom to-12% -mt-2"
    >
      <h2 className="select-none text-5xl text-center sm:text-4xl font-bold md:mt-2 sm:pt-10 sm:mb-10 xs:p-5">
        Download Drifty
      </h2>

      {/* Application Type Selection */}
      <div className="select-none grid-cols-1 justify-items-center">
        <h2 className="text-center block text-xl font-medium leading-6 text-gray-900">
          Select Application Type
        </h2>
        <div className="grid grid-cols-1 justify-items-center pb-2">
          <select
            id="listbox"
            name="Select Application Type"
            className="block w-80 px-4 py-2 mt-1 text-base text-gray-900 bg-white border-2 border-gray-300 select-none rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-700 focus:border-2 sm:text-sm"
            value={applicationType}
            onChange={handleApplicationTypeChange}
          >
            <option value="GUI">GUI</option>
            <option value="CLI">CLI</option>
          </select>
        </div>
      </div>

      {/* Download Buttons */}
      <div className="flex flex-col items-center md:flex-row md:items-start justify-center space-y-4 md:space-y-0 gap-5 pt-10">
        {/* First Download Now Button */}
        <div className="xs:mr-0 xs:mb-4">
          <div className="">
            <div className="text-center">
              <button
                className="xs:w-80 xs:py-5 bg-gradient-to-r from-blue-600 to-green-500 text-white xs:text-3xl font-semibold md:text-3xl rounded-full hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:from-pink-500 hover:to-yellow-500 hover:drop-shadow-lg focus:shadow-lg focus:outline-none active:bg-blue-400 active:shadow-lg transition"
                onClick={() =>
                  downloadLatestRelease("Windows exe", applicationType)
                }
              >
                Download Now <i className="fab fa-brands fa-windows"></i>
              </button>
              {applicationType === "GUI" && (
                <div>
                  <button
                    className="text-lg select-none text-violet-900 font-semibold hover:underline hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-0.5 hover:scale-110"
                    onClick={() => downloadLatestRelease("Windows msi")}
                  >
                    Prefer the msi?
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Second Download Now Button */}
        <div className="md:ml-4 xs:ml-0">
          <button
            className="xs:w-80 xs:py-5 bg-gradient-to-r from-blue-600 to-green-500 text-white xs:text-3xl font-semibold md:text-3xl rounded-full hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:from-pink-500 hover:to-yellow-500 hover:drop-shadow-lg focus:shadow-lg focus:outline-none active:bg-blue-400 active:shadow-lg transition"
            onClick={() => downloadLatestRelease("Linux", applicationType)}
          >
            Download Now <i className="fab fa-brands fa-linux"></i>
          </button>
        </div>

        {/* Third Download Now Button */}
        <div className="md:ml-4 xs:ml-0">
          <button
            className="xs:w-80 xs:py-5 bg-gradient-to-r from-blue-600 to-green-500 text-white xs:text-3xl font-semibold md:text-3xl rounded-full hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:from-pink-500 hover:to-yellow-500 hover:drop-shadow-lg focus:shadow-lg focus:outline-none active:bg-blue-400 active:shadow-lg transition"
            onClick={() => downloadLatestRelease("MacOS", applicationType)}
          >
            Download Now <i className="fab fa-brands fa-apple"></i>
          </button>
        </div>
      </div>

      <div>
        <h1 className="select-none text-center font-bold text-2xl pt-10">
          Past Releases
        </h1>
        {filteredReleases.map((item, index) => {
          return (
            <div key={index} className="text-center p-5 text-base font-normal">
              <span className="font-bold">{item.tag_name} </span>
              <p>
                {new Date(item.published_at).toString()} with{" "}
                {item.assets[0].download_count +
                  item.assets[1].download_count +
                  item.assets[2].download_count +
                  item.assets[3].download_count +
                  item.assets[4].download_count +
                  item.assets[5].download_count}{" "}
                Downloads
              </p>
              <button
                onClick={() => handleButtonClick(index)}
                className="text-slate-800/50"
              >
                {buttonStates[index] ? "Hide" : "Learn More"}
              </button>
              {buttonStates[index] && (
                <div
                  className="md:p-5 overflow-hidden"
                  dangerouslySetInnerHTML={{ __html: content[index] }}
                ></div>
              )}

              <div className="grid md:grid-flow-col  md:gap-16 xs:gap-3 justify-center text-white mt-3 font-semibold">
                <div className={"grid grid-cols-1 justify-items-center"}>
                  <button
                    className="select-none pl-3 pr-3 w-auto h-auto text-2xl bg-gradient-to-r from-blue-600 to-green-500 hover:from-pink-500 hover:to-yellow-500 rounded-full p-1 shadow-none hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:drop-shadow-2xl"
                    onClick={() =>
                      downloadOlderReleases(
                        "Windows exe",
                        applicationType,
                        item.tag_name
                      )
                    }
                  >
                    Download <i className="fab fa-brands fa-windows"></i>
                  </button>
                  {applicationType === "GUI" && (
                    <button
                      className={
                        "text-sm text-violet-900 font-semibold hover:underline hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-0.5 hover:scale-110"
                      }
                      onClick={() =>
                        downloadOlderReleases(
                          "Windows msi",
                          applicationType,
                          item.tag_name
                        )
                      }
                    >
                      Prefer the msi?
                    </button>
                  )}
                </div>
                <button
                  className="select-none pl-3 pr-3 w-auto h-min text-2xl bg-gradient-to-r from-blue-600 to-green-500 hover:from-pink-500 hover:to-yellow-500 rounded-full p-1 shadow-none hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:drop-shadow-2xl"
                  onClick={() =>
                    downloadOlderReleases(
                      "Linux",
                      applicationType,
                      item.tag_name
                    )
                  }
                >
                  Download <i className="fab fa-brands fa-linux"></i>
                </button>
                <button
                  className="select-none pl-3 pr-3 w-auto h-min text-2xl bg-gradient-to-r from-blue-600 to-green-500 hover:from-pink-500 hover:to-yellow-500 rounded-full p-1 shadow-none hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:drop-shadow-2xl"
                  onClick={() =>
                    downloadOlderReleases(
                      "MacOS",
                      applicationType,
                      item.tag_name
                    )
                  }
                >
                  Download <i className="fab fa-brands fa-apple"></i>
                </button>
              </div>
            </div>
          );
        })}
        {filterOlderReleases.map((item, index) => {
          index = index + filteredReleases.length;
          if (filterOlderReleases.length !== 0) {
            return (
              <div
                key={index}
                className="text-center p-5 text-base font-normal"
              >
                <span className="font-bold">{item.tag_name} </span>
                <p>
                  {new Date(item.published_at).toString()} with{" "}
                  {item.assets[0].download_count +
                    item.assets[1].download_count}{" "}
                  Downloads
                </p>
                <button
                  onClick={() => handleButtonClick(index)}
                  className="text-slate-800/50"
                >
                  {buttonStates[index] ? "Hide" : "Learn More"}
                </button>
                {buttonStates[index] && (
                  <div
                    className=" md:p-5 overflow-hidden"
                    dangerouslySetInnerHTML={{ __html: content[index] }}
                  ></div>
                )}
                <div className="grid md:grid-flow-col md:gap-52 xs:gap-8 justify-center text-white mt-3 font-semibold">
                  <button
                    className="select-none pl-3 pr-3 w-auto h-auto text-2xl bg-gradient-to-r from-blue-600 to-green-500 hover:from-pink-500 hover:to-yellow-500 rounded-full p-1 shadow-none hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:drop-shadow-2xl"
                    onClick={() =>
                      window.open(item.assets[1].browser_download_url)
                    }
                  >
                    Download <i className="fab fa-brands fa-windows"></i>
                  </button>
                  <button
                    className="select-none pl-3 pr-3 w-auto h-auto text-2xl bg-gradient-to-r from-blue-600 to-green-500 hover:from-pink-500 hover:to-yellow-500 rounded-full p-1 shadow-none hover:transition ease-in-out duration-300 delay-80 hover:-translate-y-1 hover:scale-110 hover:drop-shadow-2xl"
                    onClick={() =>
                      window.open(item.assets[0].browser_download_url)
                    }
                  >
                    Download <i className="fab fa-brands fa-apple"></i>{" "}
                    <i className="fab fa-brands fa-linux"></i>
                  </button>
                </div>
              </div>
            );
          }
        })}
      </div>
      <div className={"grid grid-cols-1 justify-items-center"}>
        <h2 className={"text-center text-sm font-bold"}>
          Looking for more releases?
        </h2>
        <a
          target={"_blank"}
          href={"https://github.com/SaptarshiSarkar12/Drifty/releases"}
          className={"text-center text-sm font-bold text-blue-500"}
        >
          View all releases
        </a>
      </div>
    </div>
  );
}
