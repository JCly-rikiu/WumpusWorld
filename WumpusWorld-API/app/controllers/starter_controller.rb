class StarterController < ApplicationController
  def init
    render json: { status: 0, data: [
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
      ] }
  end

  def resume
    if Player.exists?(name: params[:nickname])
      render json: { status: 0 }
    else
      render json: { status: 1 }
    end
  end

  def start
    nickname = params[:nickname]
    if Player.exists?(name: nickname)
      render json: { status: 0 }
    else
      Player.create(name: nickname, is_playing: true, map: create_new_game)
      render json: { status: 0 }
    end
  end

  private

  def create_new_game
    ''
  end
end