use mobile_entry_point::mobile_entry_point;
#[macro_use] extern crate log;
// use tao::{
//     event::{Event, WindowEvent},
//     event_loop::{ControlFlow, EventLoop},
//     window::WindowBuilder,
// };

#[cfg(target_os = "android")]
fn init_logging() {
    android_logger::init_once(
        android_logger::Config::default()
            .with_min_level(log::Level::Trace)
            .with_tag("w"),
    );
}

#[cfg(not(target_os = "android"))]
fn init_logging() {
    simple_logger::SimpleLogger::new().init().unwrap();
}

// #[mobile_entry_point]
// fn main() -> Result<(), Box<dyn std::error::Error>> {
//     init_logging();
//     let event_loop = EventLoop::new();
//
//     // let window = WindowBuilder::new()
//     //     .with_title("A fantastic window!")
//     //     .with_inner_size(tao::dpi::LogicalSize::new(128.0, 128.0))
//     //     .build(&event_loop)
//     //     .unwrap();
//
//     // Create a VM for executing Java calls
//     let native_activity = ndk_glue::native_activity();
//     let vm_ptr = native_activity.vm();
//     let vm = unsafe { jni::JavaVM::from_raw(vm_ptr) }?;
//     let env = vm.attach_current_thread()?;
//
//     let class = env.find_class("android/content/Intent")?;
//     let activity = env.find_class("com/example/w/MainActivity")?;
//     // let intent = env.new_object(class, "(Landroid/content/Context;jclass)V", &[native_activity.activity().into(), activity.into()])?;
//     // let x = env.call_method(native_activity.activity(), "startActivity", "(Landroid/content/Intent;)V", &[intent.into()])?;
//     Ok(())
//
//     // event_loop.run(move |event, _, control_flow| {
//     //     *control_flow = ControlFlow::Wait;
//     //     println!("{:?}", event);
//     //
//     //     match event {
//     //         Event::WindowEvent {
//     //             event: WindowEvent::CloseRequested,
//     //             window_id,
//     //             ..
//     //         } if window_id == window.id() => *control_flow = ControlFlow::Exit,
//     //         Event::MainEventsCleared => {
//     //             window.request_redraw();
//     //         }
//     //         _ => (),
//     //     }
//     // });
// }

use jni::objects::{JClass, JString, JObject};
use jni::sys::{jstring, jobject};
use jni::JNIEnv;
use wry::{application::window::Window, webview::*};

#[no_mangle]
pub unsafe extern "C" fn Java_com_example_w_WryWebView_create(
    env: JNIEnv,
    jclass: JClass,
    jobject: JObject,
) -> jobject {
    init_logging();
    let webview = WebViewBuilder::new(Window::new()).unwrap()
        //.with_url("https://google.com").unwrap()
        .with_ipc_handler(|_, message| {
            debug!("{}", message);
        })
        .build().unwrap();
    debug!("111fskfkdjfksjdl");
    webview.run(env, jclass, jobject)
}

#[no_mangle]
pub unsafe extern "C" fn Java_com_example_w_IpcHandler_ipc(
    env: JNIEnv,
    _jclass: JClass,
    jobject: JString,
) {
    let window = Window::new();
    let arg = env.get_string(jobject).unwrap().to_str().unwrap().to_string();
    WebView::ipc_handler(&window, arg);
}
