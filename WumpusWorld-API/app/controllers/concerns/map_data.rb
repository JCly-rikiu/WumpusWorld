module Map_data

  extend ActiveSupport::Concern

  @@positions = [
    {
      latitude: 25.021651,
      longitude: 121.535189,
      title: '新體'
    },
    {
      latitude: 25.020344,
      longitude: 121.537628,
      title: '醉月湖'
    },
    {
      latitude: 25.020708,
      longitude: 121.536405,
      title: '游泳池'
    },
    {
      latitude: 25.019726,
      longitude: 121.536346,
      title: '舊體'
    },
    {
      latitude: 25.018894,
      longitude: 121.536302,
      title: '博雅'
    },
    {
      latitude: 25.018633,
      longitude: 121.537266,
      title: '小福'
    },
    {
      latitude: 25.018558,
      longitude: 121.536801,
      title: '普通'
    },
    {
      latitude: 25.017174,
      longitude: 121.536749,
      title: '傅鐘'
    },
    {
      latitude: 25.016280,
      longitude: 121.536781,
      title: '行政大樓'
    },
    {
      latitude: 25.017286,
      longitude: 121.540573,
      title: '總圖'
    },
    {
      latitude: 25.016862,
      longitude: 121.539610,
      title: '保健中心'
    }
  ]

  def get_positions
    @@positions
  end

end
