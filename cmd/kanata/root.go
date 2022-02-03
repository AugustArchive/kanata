// 💫 Kanata: Automative Kubernetes watcher to view pod phases and reflect them onto popular statuspages.
// Copyright (c) 2021-2022 Noel <cutie@floofy.dev>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package kanata

import "github.com/spf13/cobra"

var (
	rootCmd = &cobra.Command{
		Use:   "kanata",
		Short: "Automative Kubernetes watcher to view pod phases and reflect them onto popular statuspages.",
		RunE:  run,
	}

	configFilePath string
	debug          bool
)

func run(_ *cobra.Command, _ []string) error {
	return nil
}

func init() {
	rootCmd.Flags().StringVarP(&configFilePath, "config", "c", "./config.toml", "the configuration file path to use")
	rootCmd.Flags().BoolVarP(&debug, "debug", "d", false, "if debug logs should be used. over-ridden if `debug` in config.toml exists.")
	rootCmd.AddCommand(
		newValidateCommand(),
		newGenerateCommand(),
		newPingCommand(),
	)
}

// Execute runs the root cobra.Command
func Execute() int {
	if err := rootCmd.Execute(); err != nil {
		return 1
	}

	return 0
}
