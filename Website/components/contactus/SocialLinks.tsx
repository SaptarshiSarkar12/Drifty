import { FaGithub, FaDiscord, FaXTwitter } from "react-icons/fa6";

export default function SocialLinks() {
  return (
    <div className="p-6 rounded-lg shadow-lg">
      <h2 className="text-2xl font-semibold py-4">Connect with us</h2>
      <div className="flex space-x-4">
        <a
          href="https://www.discord.com"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#5865f2] dark:text-gray-400 dark:hover:text-[#5865f2]"
          aria-label="Discord"
        >
          <FaDiscord className="h-8 w-8" />
        </a>
        <a
          href="https://github.com"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#1B1F24] dark:text-gray-400 dark:hover:text-[#ffffff]"
          aria-label="GitHub"
        >
          <FaGithub className="h-8 w-8" />
        </a>
        <a
          href="https://www.twitter.com"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-500 hover:text-[#000000] dark:text-gray-400 dark:hover:text-[#ffffff]"
          aria-label="Twitter"
        >
          <FaXTwitter className="h-8 w-8" />
        </a>
      </div>
    </div>
  );
}
