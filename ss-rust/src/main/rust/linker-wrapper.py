from __future__ import absolute_import, print_function, unicode_literals

import os
import shutil
import subprocess
import sys
import shlex  # Replaces pipes for quoting

args = [os.environ['RUST_ANDROID_GRADLE_CC'], os.environ['RUST_ANDROID_GRADLE_CC_LINK_ARG']] + sys.argv[1:]

# Use shlex.quote instead of pipes.quote
printable_cmd = ' '.join(shlex.quote(arg) for arg in args)
print(printable_cmd)

code = subprocess.call(args)
if code == 0:
    try:
        output_index = sys.argv.index('-o')
        output_file = sys.argv[output_index + 1]
        shutil.copyfile(output_file, os.environ['RUST_ANDROID_GRADLE_TARGET'])
    except (ValueError, IndexError) as e:
        print(f"Error: Could not find output file or invalid arguments: {e}", file=sys.stderr)
        sys.exit(1)
sys.exit(code)
