/** @type {import('next').NextConfig} */

module.exports = {
  assetPrefix: '/Drifty',
  basePath: '/Drifty',
  images: {
    domains: ['avatars.githubusercontent.com','camo.githubusercontent.com','cdn.jsdelivr.net'],
    dangerouslyAllowSVG: true,
    unoptimized: true,
  },
}
