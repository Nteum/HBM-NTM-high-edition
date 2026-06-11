import os


def convert_resources_to_lowercase(target_dir):
    """递归遍历指定目录，将所有文件和文件夹名称转换为小写"""
    if not os.path.exists(target_dir):
        print(f" 错误：找不到目标路径 '{target_dir}'，请检查是否输入正确。")
        return

    print(f" 开始扫描并转换资源路径: {target_dir}")
    print("-" * 50)

    # 1. 首先处理【文件】的重命名
    # 使用 os.walk(topdown=False) 的目的是从最内层的子目录开始往外处理，防止父目录改名后导致内层路径失效
    file_counter = 0
    for root, dirs, files in os.walk(target_dir, topdown=False):
        for file in files:
            # 检查文件名中是否包含大写字母
            if any(c.isupper() for c in file):
                old_file_path = os.path.join(root, file)
                new_file_name = file.lower()
                new_file_path = os.path.join(root, new_file_name)

                # # 处理可能存在的同名冲突（例如同时存在 test.png 和 TEST.png 的极端情况）
                # if os.path.exists(new_file_path) and old_file_path != new_file_path:
                #     print(
                #         f"?? 警告: 小写冲突！'{new_file_name}' 已存在，跳过覆盖。"
                #     )
                #     continue

                os.rename(old_file_path, new_file_path)
                print(f"? 文件改名: {file}  ==>  {new_file_name}")
                file_counter += 1

    # 2. 紧接着处理【文件夹】的重命名
    dir_counter = 0
    for root, dirs, files in os.walk(target_dir, topdown=False):
        for directory in dirs:
            if any(c.isupper() for c in directory):
                old_dir_path = os.path.join(root, directory)
                new_dir_name = directory.lower()
                new_dir_path = os.path.join(root, new_dir_name)

                if os.path.exists(new_dir_path) and old_dir_path != new_dir_path:
                    print(
                        f"?? 警告: 文件夹小写冲突！'{new_dir_name}' 已存在，跳过。"
                    )
                    continue

                os.rename(old_dir_path, new_dir_path)
                print(f"? 文件夹改名: {directory}  ==>  {new_dir_name}")
                dir_counter += 1

    print("-" * 50)
    print(
        f"? 转换完成！共修改了 {file_counter} 个文件，{dir_counter} 个文件夹。"
    )


if __name__ == "__main__":
    # ? 建议：把当前脚本和你的 resources 目录放在一起运行
    # 如果不是标准结构，可以直接修改下方的路径字符串，例如 r"D:\ModProject\src\main\resources"
    target_path = "../src/main/resources/assets/hbm/textures"

    # 执行转换
    convert_resources_to_lowercase(target_path)