{
  description = "My Android project";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs";
    devshell.url = "github:numtide/devshell";
    flake-utils.url = "github:numtide/flake-utils";
    android.url = "github:tadfisher/android-nixpkgs";
  };

  outputs = { self, nixpkgs, devshell, flake-utils, android }:
    {
      overlay = final: prev: {
        inherit (self.packages.${final.system}) android-sdk android-studio;
      };
    }
    //
    flake-utils.lib.eachSystem [ "aarch64-darwin" "x86_64-darwin" "x86_64-linux" ] (system:
      let
        inherit (nixpkgs) lib;
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
          overlays = [
            devshell.overlays.default
            self.overlay
          ];
        };
      in
      {
        packages = {
          android-sdk = android.sdk.${system} (sdkPkgs: with sdkPkgs; [
            # Useful packages for building and testing.
            build-tools-36-1-0
            cmdline-tools-latest
            platform-tools
            platforms-android-36
            skiaparser-3
            sources-android-36
          ]);
        };

        devShell = import ./devshell.nix { inherit pkgs; };
      }
    );
}
