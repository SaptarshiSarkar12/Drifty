import { FaGithub, FaDiscord, FaXTwitter, FaLinkedin } from "react-icons/fa6";

export default function SocialLinks() {
  return (
    <div className="p-6 rounded-lg shadow-lg">
      <h2 className="text-2xl font-semibold py-4">Connect with us</h2>
      <div className="flex space-x-4">
        <a
          href="https://discord.gg/DeT4jXPfkG"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#5865f2] dark:text-gray-400 dark:hover:text-[#5865f2]"
          aria-label="Discord"
        >
          <FaDiscord className="h-8 w-8" />
        </a>
        <a
          href="https://github.com/SaptarshiSarkar12/Drifty"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#1B1F24] dark:text-gray-400 dark:hover:text-[#ffffff]"
          aria-label="GitHub"
        >
          <FaGithub className="h-8 w-8" />
        </a>
        <a
          href="https://www.x.com/SSarkar2007"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#000000] dark:text-gray-400 dark:hover:text-[#ffffff]"
          aria-label="X"
        >
          <FaXTwitter className="h-8 w-8" />
        </a>
        <a
          href="https://www.linkedin.com/in/saptarshisarkar12/"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#000000] dark:text-gray-400 dark:hover:text-[#ffffff]"
          aria-label="X"
        >
          <FaLinkedin className="h-8 w-8" />
        </a>
      </div>
    </div>
  );
}
