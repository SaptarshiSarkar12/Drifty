/** @type {import('next').NextConfig} */
const nextConfig = {
    assetPrefix: '/Drifty/',
    output: 'export',
    images: {
        remotePatterns: [
            {
                protocol: 'https',
                hostname: 'avatars.githubusercontent.com',
            },
            {
                protocol: 'https',
                hostname: 'camo.githubusercontent.com',
            }
        ],
        dangerouslyAllowSVG: true,
        unoptimized: true
    },
}

module.exports = nextConfig
