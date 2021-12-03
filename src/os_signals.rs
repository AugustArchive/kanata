use libc::{c_int, sighandler_t, signal, SIGINT, SIGTERM};
use log::warn;

extern "C" fn handler(_: c_int) {
    warn!("exiting process...");
    std::process::exit(0);
}

/// Applies the signals ([SIGINT], [SIGTERM]) to exit the process.
pub(crate) unsafe fn init_signals() {
    signal(SIGINT, handler as extern "C" fn(_) as sighandler_t);
    signal(SIGTERM, handler as extern "C" fn(_) as sighandler_t);
}
